package org.zerock.apiServer.config;


//formatter를 실행시켜주기 위한 config

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zerock.apiServer.controller.formatter.LocalDateFormatter;

@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        log.info("--------------------------");
        log.info("addFormatters");
        registry.addFormatter(new LocalDateFormatter());
    }

//    CORS(Cross-Origin Resource Sharing) 다른 출처로부터 자원 공유를 허락하기 위한 것
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 적용할 것이다.
        registry.addMapping("/**")
                // 어디서 부터 들어오는 경로를 허락할겁니까
                .allowedOrigins("*")
                .maxAge(500)
                // 어떤 방식을 허용할 것이냐
                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS");
    }
}
