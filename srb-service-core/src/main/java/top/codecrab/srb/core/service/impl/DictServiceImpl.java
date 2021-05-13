package top.codecrab.srb.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.srb.common.config.Constants;
import top.codecrab.srb.common.excetion.Assert;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;
import top.codecrab.srb.common.utils.CommonUtils;
import top.codecrab.srb.core.entity.Dict;
import top.codecrab.srb.core.entity.dto.ExcelDictDTO;
import top.codecrab.srb.core.entity.vo.DictVo;
import top.codecrab.srb.core.listener.ExcelDictDtoListener;
import top.codecrab.srb.core.mapper.DictMapper;
import top.codecrab.srb.core.service.DictService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file) {
        Assert.notNull(file, ResponseEnum.UPLOAD_ERROR);
        try {
            ExcelReaderBuilder readerBuilder = EasyExcel.read(file.getInputStream(), ExcelDictDTO.class, new ExcelDictDtoListener(baseMapper));
            String fileType = FileUtil.getSuffix(file.getOriginalFilename());

            if (!StrUtil.equalsAny(fileType, Constants.FILE_TYPE_XLS, Constants.FILE_TYPE_XLSX)) {
                throw new BusinessException(ResponseEnum.FILE_TYPE_MISMATCH_ERROR);
            }

            // 如果是03版的excel文件，就使用excelType指定ExcelTypeEnum.XLS
            if (StrUtil.equals(Constants.FILE_TYPE_XLS, fileType)) {
                readerBuilder.excelType(ExcelTypeEnum.XLS);
            }
            readerBuilder.sheet().doRead();
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @Override
    public List<ExcelDictDTO> findAllExcelDictDTO() {
        return baseMapper.selectList(Wrappers.emptyWrapper()).stream().map(dict -> {
            ExcelDictDTO dictDTO = new ExcelDictDTO();
            BeanUtil.copyProperties(dict, dictDTO);
            return dictDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DictVo> listByParentId(Long parentId, Boolean isLazy) {
        // 默认为不使用懒加载的key
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(Constants.REDIS_SRB_CORE_DICT_LIST_KEY_FAST_LOAD);

        if (isLazy == null) {
            isLazy = false;
        } else if (isLazy && parentId == null) {
            throw new BusinessException(ResponseEnum.LAZY_LOAD_WITH_PARENT_ID_NULL_ERROR);
        } else if (isLazy) {
            //懒加载
            boundValueOps = redisTemplate.boundValueOps(Constants.REDIS_SRB_CORE_DICT_LIST_KEY + parentId);
        }

        try {
            // 根据是否为懒加载获取redis中的值
            List<DictVo> dictVos = CommonUtils.castList(boundValueOps.get(), DictVo.class);
            if (CollectionUtil.isNotEmpty(dictVos)) {
                log.info("从缓存中获取。。。");
                return dictVos;
            }
        } catch (Exception e) {
            log.error("redis服务器连接失败：" + ExceptionUtils.getStackTrace(e));
        }

        // 如果是懒加载，就根据父id查询，不是懒加载就不查询父id为0的
        List<Dict> dictList = baseMapper.selectList(new QueryWrapper<Dict>()
                .eq(isLazy, "parent_id", parentId)
                .ne(!isLazy, "parent_id", 0)
        );

        List<DictVo> dictVoList;
        if (isLazy) {
            // 懒加载处理
            dictVoList = dictList.stream().map(dict -> {
                DictVo vo = new DictVo();
                BeanUtil.copyProperties(dict, vo);
                vo.setHasChildren(this.hasChildren(dict.getId()));
                return vo;
            }).collect(Collectors.toList());
        } else {
            // 立即加载处理
            dictVoList = this.packageTree(dictList);
        }

        try {
            // 存入redis
            log.info("数据字典列表存入redis中");
            boundValueOps.set(dictVoList, 3, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis服务器连接失败：" + ExceptionUtils.getStackTrace(e));
        }

        return dictVoList;
    }

    @Override
    public List<DictVo> findByDictCode(String dictCode) {
        Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                .eq("dict_code", dictCode));

        Assert.notNull(dict, ResponseEnum.DICT_CODE_NOT_FIND_ERROR);
        return this.listByParentId(dict.getId(), true);
    }

    @Override
    public Map<String, Object> findByDictCodes(List<String> dictCodes) {
        return dictCodes.stream().collect(Collectors.toMap(
                Function.identity(),
                this::findByDictCode,
                (oldVal, currVal) -> currVal)
        );
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        Dict parentDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                .eq("dict_code", dictCode));
        if (parentDict == null) {
            return "";
        }

        Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                .eq("parent_id", parentDict.getId())
                .eq("value", value));
        if (dict == null) {
            return "";
        }

        return dict.getName();
    }

    /**
     * 根据id查询数据库中parent_id字段中是否有记录
     *
     * @param id 数据id
     * @return 是否有记录
     */
    private boolean hasChildren(Long id) {
        return baseMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id)) > 0;
    }

    /**
     * 将实体类Dict的数据列表转为树形结构
     *
     * @param dictList Dict列表
     * @return 树形结构的Dict列表
     */
    private List<DictVo> packageTree(List<Dict> dictList) {
        // 转为DictVo集合
        List<DictVo> dictVos = dictList.stream().map(dict -> {
            DictVo vo = new DictVo();
            BeanUtil.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());

        // 拷贝一份元数据
        List<DictVo> result = new ArrayList<>(dictVos);

        for (DictVo parent : dictVos) {
            // 创建装载子列表的集合
            for (DictVo child : dictVos) {
                if (parent.getId().equals(child.getParentId())) {
                    parent.getChildren().add(child);
                    // 添加过后在返回集合中删除该子节点 由于对象的内存地址是一样的，所以可以操作该集合
                    result.remove(child);
                }
            }
        }
        return result;
    }

}
