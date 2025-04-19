package com.example.globus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.boot.starter.operation.WebFluxOperationScanner;
import springfox.boot.starter.operation.WebMvcOperationScanner;
import springfox.documentation.swagger.configuration.SwaggerWebFluxConfiguration;
import springfox.documentation.swagger.configuration.SwaggerWebMvcConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Value("${swagger.enable:true}")
    private boolean enableSwagger;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(info())
                .servers(Arrays.asList(new Server().url("https://your-api-host.com")))
                .components(new Components());
    }

    private Info info() {
        return new Info()
                .title("Financial Monitoring and Reporting API")
                .description("API for financial monitoring and reporting")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Our company")
                        .url("https://our-company.com")
                        .email("support@our-company.com"))
                .license(license());
    }

    @Bean
    public WebMvcOperationScanner operationScanner() {
        return new WebMvcOperationScanner();
    }

    @Bean
    public SwaggerWebMvcConfiguration swaggerWebMvcConfiguration() {
        return new SwaggerWebMvcConfiguration();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .resourceChain(false);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
