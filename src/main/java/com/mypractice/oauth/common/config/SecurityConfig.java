package com.mypractice.oauth.common.config;

import com.mypractice.oauth.common.auth.JwtTokenFilter;
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
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 여기서 만들어지는 객체를 빈에 등록!
    }

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource())) // 같은 도메인 끼리만 api를 통해서 데이터를 주고받겠다는 설정
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화 => MVC 패턴에 대한 공격 방어를 끄겠다
                // Basic 인증 비활성화
                // Basic 인증은 사용자이름과 비밀번호를 Base64로 인증값으로 사용하는것
                // 우린 토큰으로 할거라서 토큰은 signature 부분에 암호화가 들어가기때문에 Basic 인증과 다르다
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 방식을 비활성화 하겠다.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 특정 url패턴에 대해서는 인증처리 제외 => 인증처리: 서버에서 authen tication 객체 생성이다.
                // 사용자가 로그인을 하면 서버에서 토큰을 줌 추후에 api 요청을 할때마다 + token을 얹어서 보낸다.
                // 서버는 그 토큰을 까봐야한다. 그 토큰을 만뜰때 쓴 secretkey와 사용자가 보낸거가 같은지 검증해야한다.
                // 검증에서 성공하고 나면 서버의 메모리에 authentication 객체를 만들어 줄것이다.
                // 그러면 spring security 의존성에서 authentication객체가 만들어 진것을 확인하고 로그인 성공했다고 판단한다.
                // 어찌 됐든 지금 아래 authorizeHttpRequest => 요놈은 해당 애들은 authentication 객체가 없더라도 API허용
                .authorizeHttpRequests(a -> a.requestMatchers("/member/create", "/member/doLogin", "/member/google/doLogin", "/member/kakao/doLogin").permitAll().anyRequest().authenticated())
                // UsernamePasswordAuthenticationFilter 에서 form login 인증을 처리 근데 이건 mvc pattern에서 쓰는 spring에서 제공하는 로그인화면
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));// 더떤 도메인을 허락할꺼냐 인데. 도메인이 여러개일 수 있으므로 list형태로 받음
        configuration.setAllowedMethods(Arrays.asList("*")); // 어떤 HTTP 메서드를 허용할것이냐(post, get, put, patch, delete), "*" 모든 메서드 쓰겠다.
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더값를 허용하겠다.
        configuration.setAllowCredentials(true); // 자격 증명 허용하겠다 -> 보안상 내가 자격이 있다라는 것을 증명하는 값

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url pattern에 대해서 cors 허용 설정
        return source;
    }
}
