package com.example.globus.config;

import io.swagger.v3.oas.models.info.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Value("${swagger.enable:true}")
    private boolean enableSwagger;

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.globus.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData())
                .enable(enableSwagger)
                .enableUrlTemplating(true)
                .protocols(getSupportedProtocols())
                .host("your-api-host.com");
    }

    private Set<String> getSupportedProtocols() {
        Set<String> protocols = new HashSet<>();
        protocols.add("https");
        protocols.add("http");
        return protocols;
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Financial Monitoring and Reporting API")
                .description("API для финансового мониторинга и отчетности")
                .version("1.0.0")
                .contact(new Contact(
                        "Our company",
                        "https://our-company.com",
                        "support@our-company.com"
                ))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
