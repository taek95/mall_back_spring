package org.zerock.apiServer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.apiServer.security.filter.JWTCheckFilter;
import org.zerock.apiServer.security.handler.APILoginFailHandler;
import org.zerock.apiServer.security.handler.APILoginSuccessHandler;
import org.zerock.apiServer.security.handler.CustomAccessDeniedHandler;

import java.util.Arrays;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("--------------security config---------------");

        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        // 세션 만들지 말라는 설정
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        http.formLogin(config -> {
            config.loginPage("/api/member/login");
            // 성공하면 뭐가 동작하게 할거야? -> 파라미터 들어오는 걸로 해결 할거야~
            config.successHandler(new APILoginSuccessHandler());
            // 실패시에 200코드가 보내지지만 실패 메시지를 보낸다.
            config.failureHandler(new APILoginFailHandler());
        });


        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 모든 애들 다 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // header 메시지 만들기
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
