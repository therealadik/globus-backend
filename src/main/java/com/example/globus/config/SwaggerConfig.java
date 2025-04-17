package com.example.globus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import static jdk.internal.joptsimple.util.RegexMatcher.regex;

@Configuration
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket productApi() {
        /*создание контейнера для API окументации*/
        return new Docket (DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.globus.controller"))
                .paths(regex("/")) /*десь должно быть название эндпоинтов*/
                .build()
                .apiInfo(metaData);
    }
    private ApiInfo metaData(){
        return ApiInfoBuilder()
                .title("")
                .description("\"financial monitoring and reporting API")
    }

}
