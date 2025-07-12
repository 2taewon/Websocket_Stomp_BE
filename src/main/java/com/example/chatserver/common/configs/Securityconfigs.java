package com.example.chatserver.common.configs;

import com.example.chatserver.common.auth.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class Securityconfigs {

    private final JwtAuthFilter jwtAuthFilter;
    @Bean // bean이 붙어있는 메서드가 리턴하주는 객체를 싱글톤 객체로 만들겠다. 메서드 객체 x -> 클래스가 객체 O
    public SecurityFilterChain myFilter(HttpSecurity http) throws Exception {
        return http
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화
                //특정 url 패턴에 대해서는 Authentication 객체 요구하지 않는다.(인증처리 제외)
                .authorizeHttpRequests(authorizeRequests ->authorizeRequests.requestMatchers("/member/create", "/member/doLogin").permitAll().anyRequest().authenticated())
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 방식을 사용하지 않겠다는 의미
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // 특정 url에 대한 것은 빼고 나머지 url은 토큰을 검증하는데 jwtAuthFilter 클래스에서 검증한다.
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); //HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더값 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url에 패턴에 대해 cors 허용
        return source;
    }

    @Bean // 싱글톤 객체로 패스워드가 만들어 짐 비밀번호를 암호화 해서 데이터베이스에 집어 넣기 위함
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
