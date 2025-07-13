package com.example.chatserver.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {
   @Value("${jwt.secretKey}")
   private String secretKey;

    @Override // 토큰을 받아서 들어올 때, 우리 서버에서 만든 토큰인지 확인
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = request.getHeader("Authorization");

        try{
            //토큰이 비어있지 않고 토큰의 접두사가 Bearer 로 시작하지 않는다면 에러
            if (token != null) {
                if (!token.startsWith("Bearer ")) {
                    throw new AuthenticationException("Bearer token 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                // Jwts에서 파싱해서 우리가 만든 토큰인지 아닌지 검증 해주고 검증이 끝나면 getBody를 통해서 claims 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

                // Authentication 객체 생성
                // auth에 이메일과 권한이 들어가 있음 -> 스프링 전역에서 사용가능 SecurityContextHolder.getContext().getAuthentication(auth);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ " + claims.get("role").toString()));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("invalid token");
        }
    }
}
