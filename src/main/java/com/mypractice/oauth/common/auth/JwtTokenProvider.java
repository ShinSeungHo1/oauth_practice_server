package com.mypractice.oauth.common.auth;

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

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName()); // decode를 시키고 다음에 어떤 알고리즘으로 암호화 해주는 것
        // jwt token에서 secret 부분!!
    }

    public String createToken(String email, String role) {
        // claims는 jwt토큰의 payload 부분을 의미한다!!!!!!!!!!!!!
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date(); //발행 시간
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발행 시간
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L)) // 만료시간 설정인데 현재 시간에 내가 설정 시간을 밀리초로 계산해서 줘야한다. yml에서 설정한건 분단위기 때문에 계산 해줘야함
                .signWith(SECRET_KEY) // signature 부분 만드는건데 우리가 만들어준 SECRET_KEY를 넣어주자
                .compact();

        return token;
    }
}
