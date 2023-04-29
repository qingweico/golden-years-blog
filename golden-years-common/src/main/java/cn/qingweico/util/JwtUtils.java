package cn.qingweico.util;

import cn.qingweico.global.SysConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * jwt生成接口
 *
 * @author zqw
 * @date 2021/11/10
 */
@Slf4j
public class JwtUtils {
    /**
     * 私钥
     */
    private final static String BASE_64_SECRET = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

    /**
     * 过期时间(两小时)
     */
    private final static int EXPIRES_SECOND = 1000 * 60 * 2 * 60;

    /**
     * 解析jwt token 获取数据
     *
     * @param jsonWebToken token
     * @return Claims
     */
    public static Claims parseJwt(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(BASE_64_SECRET))
                    .parseClaimsJws(jsonWebToken)
                    .getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 生成jwt token user
     *
     * @param userId userId
     * @return token
     */
    public static String createJwt(String userId) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE_64_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim(SysConst.USER_ID, userId)
                .signWith(signatureAlgorithm, signingKey);
        // 添加Token过期时间
        if (EXPIRES_SECOND >= 0) {
            long expMillis = nowMillis + EXPIRES_SECOND;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        // 生成JWT
        String compact = builder.compact();
        log.info("生成 token: {}", compact);
        return compact;
    }


    /**
     * JWT 是否过期
     * @param claims Claims
     * @return JWT : true 过期 | false 未过期
     */
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
