package kr.ac.jbnu.cr.bookstore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().replaceWithClass(Pageable.class, PageableAsQueryParam.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bookstore API")
                        .version("1.0")
                        .description("API de gestion de librairie"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private static class PageableAsQueryParam {
        @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
        private Integer page;

        @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "20")
        private Integer size;

        @io.swagger.v3.oas.annotations.Parameter(description = "Sort (field,direction)", example = "createdAt,desc")
        private String sort;
    }
}