package pers.tgl.mikufans.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class JwtUtils {
    public static String createJwt(String subject, Duration timeout, String secret) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, Long.valueOf(timeout.getSeconds()).intValue());
        return Jwts.builder()
                .setHeaderParam("typ", "jwt")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(calendar.getTime()) //到期时间
                .setSubject(subject)
                .signWith(SignatureAlgorithm.HS256, secret) //加密
                .compact();
    }

    /**
     * 解析jwt的内容，可能会抛出异常 JwtException
     */
    public static Claims parseJwt(String jwt, String secret) throws JwtException {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }
}