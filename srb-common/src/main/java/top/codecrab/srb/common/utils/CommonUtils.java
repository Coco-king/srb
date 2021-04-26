package top.codecrab.srb.common.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author codecrab
 */
public class CommonUtils {

    /**
     * 获取客户端真实IP
     */
    public static String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取一言的sign
     */
    public static String getSign() {
        String url = "https://v1.hitokoto.cn?encode=json&charset=utf-8&c=a&c=b&c=d&c=e&c=f&c=g&c=h&c=i&c=j&c=k&c=l&c=c";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.exchange(URI.create(url), HttpMethod.GET, new HttpEntity<>(null), String.class);
        return MapUtil.getStr(JSONUtil.toBean(response.getBody(), Map.class), "hitokoto");
    }

    /**
     * 把以obj形式存在的list对象转为list
     *
     * @param obj   list
     * @param clazz list中的对象类型字节码
     * @param <T>   对象类型
     * @return list集合
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
