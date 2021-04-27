package top.codecrab.srb.base.utils;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.*;
import top.codecrab.srb.common.excetion.BusinessException;
import top.codecrab.srb.common.response.ResponseEnum;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * @author codecrab
 */
public class JwtUtils {

    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;
    private static final String TOKEN_SIGN_KEY = "srb@Login(Auth}*^31)&codecrab%";

    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(TOKEN_SIGN_KEY);
        return new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
    }

    public static String createToken(Long userId, String userName) {
        return Jwts.builder()
                .setSubject("SRB-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, getKeyInstance())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static String createToken(Long userId, String userName, Map<String, Object> params) {
        return Jwts.builder()
                .setClaims(params)
                .setSubject("SRB-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, getKeyInstance())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    /**
     * 判断token是否有效
     */
    public static boolean checkToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token) {
        Claims claims = getClaims(token);
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    public static String getUserName(String token) {
        return (String) getClaims(token).get("userName");
    }

    public static void removeToken(String token) {
        //jwtToken无需删除，客户端扔掉即可。
    }

    /**
     * 校验token并返回Claims
     */
    private static Claims getClaims(String token) {
        if (StrUtil.isBlank(token)) {
            // LOGIN_AUTH_ERROR(-211, "未登录"),
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }
}

