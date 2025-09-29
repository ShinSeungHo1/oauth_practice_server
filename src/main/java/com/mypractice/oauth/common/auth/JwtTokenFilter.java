package com.mypractice.oauth.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 토큰 검증하는 로직이 들어갈 것
// /member/create and /member/login은 authentication 객체가 필요없지만 나머지 요청은 authentication 객체가 필요하기 때문에 검증이 있어야함!
@Component
public class JwtTokenFilter extends GenericFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = httpServletRequest.getHeader("Authorization");
        try {
            if(token != null) {
                // 우리는 token을 생성할때 Bearer ~~~~~ 라고 만드는게 관례다
                if(!token.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);
                // token 검증 및 claims(payload) 추출
                Claims claims = Jwts.parserBuilder() //  Claims가 뭐라고 jwt에서  payload 부분이다.
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken) // 여기 까지 검증하는거
                        .getBody();
                //Authentication 객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role"))); //ROLE_ 이것도 관례다
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
                // authentication 사용법
                //SecurityContextHolder.getContext().getAuthentication().getName();         => claims.getSubject();
                //SecurityContextHolder.getContext().getAuthentication().getCredentials();  => jwtToken
                //SecurityContextHolder.getContext().getAuthentication().getAuthorities();  => userDetails.getAuthorities()
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch(Exception e) {
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("invalid token");
        }

    }
}
