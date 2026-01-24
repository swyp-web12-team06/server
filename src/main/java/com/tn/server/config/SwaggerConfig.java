package com.tn.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile({"local", "dev"})
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PromptLook Server API Docs")
                        .description("스위프 웹 12기 PromptLook 프로젝트 API 명세서")
                        .version("v1.0.0"))
                // JWT 토큰 인증 설정 (헤더에 'Authorization' 버튼 생성)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))

                // 서버 URL 설정 (나중에 HTTPS 적용 시 여기만 바꾸면 됨)
                .servers(List.of(
                        new Server().url("/").description("현재 서버 (Relative Path)")
                ));
    }
}