package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;

    // yml파일에서 값을 주입받음
    public JwtTokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        // HS512 알고리즘을 가지고 SecretKey를 암호화 할 것이다. 이후 SECRET_KEY를 가져다가 쓴다.
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email); // claims는 jwt에서 payload 와 같다. key값을 하나 정해서 넘긴다. 여기선 email
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expiration*60*1000L)) //밀리 초 단위까지 세팅
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }
}
