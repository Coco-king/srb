package top.codecrab.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.codecrab.srb.core.entity.BorrowInfo;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-04-22
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    /**
     * 查询BorrowInfo扩展的列表
     *
     * @return BorrowInfo扩展列表
     */
    List<BorrowInfo> selectBorrowInfoList();
}
