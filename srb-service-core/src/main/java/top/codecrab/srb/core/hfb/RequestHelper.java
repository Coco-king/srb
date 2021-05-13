package top.codecrab.srb.core.hfb;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import top.codecrab.srb.common.utils.HttpUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author codecrab
 */
@Slf4j
public class RequestHelper {

    /**
     * 请求数据获取签名
     */
    public static String getSign(Map<String, Object> paramMap) {
        paramMap.remove("sign");
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");
        }
        str.append(HfbConst.SIGN_KEY);
        log.info("加密前：" + str.toString());
        String md5Str = SecureUtil.md5(str.toString());
        log.info("加密后：" + md5Str);
        return md5Str;
    }

    /**
     * Map转换
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>(paramMap.size());
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 签名校验
     */
    public static boolean isSignEquals(Map<String, Object> paramMap) {
        String sign = (String) paramMap.get("sign");
        String md5Str = getSign(paramMap);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 封装同步请求
     */
    public static JSONObject sendRequest(Map<String, Object> paramMap, String url) {
        String result = "";
        try {
            //封装post参数
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postData.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }
            log.info(String.format("--> 发送请求到汇付宝：post data %1s", postData));
            byte[] reqData = postData.toString().getBytes(StandardCharsets.UTF_8);
            byte[] respData = HttpUtils.doPost(url, reqData);
            result = new String(respData);
            log.info(String.format("--> 汇付宝应答结果：result data %1s", result));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JSONUtil.parseObj(result);
    }
}
