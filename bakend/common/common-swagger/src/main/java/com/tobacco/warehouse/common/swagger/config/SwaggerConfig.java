package com.tobacco.warehouse.common.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 *
 * @author warehouse
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("延安烟厂三维数字孪生系统 API")
                        .description("烟厂仓库系统后端API接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("warehouse")
                                .email("warehouse@tobacco.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {
                    pathItem.readOperations().forEach(operation -> {
                        operation.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("Authorization")
                                .in("header")
                                .required(false)
                                .schema(new io.swagger.v3.oas.models.media.StringSchema()
                                        .type("string")
                                        .description("JWT Token")));
                    });
                });
            }
        };
    }
}
