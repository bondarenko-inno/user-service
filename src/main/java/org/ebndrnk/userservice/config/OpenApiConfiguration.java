package org.ebndrnk.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfiguration
 * <p>
 * This configuration class sets up the OpenApi's beans
 * for OpenAPI configuration for API documentation.
 */
@Configuration
public class OpenApiConfiguration {


    private static final String SECURITY_SCHEME_NAME = "Bearer";

    /**
     * Creates an OpenAPI bean for API documentation.
     * The server URL is injected from the property 'site.domain.url'.
     *
     * @param api the base API URL from configuration
     * @return a configured OpenAPI instance with server details
     */
    @Bean
    OpenAPI prodOpenAPI(@Value("${site.domain.url}") String api) {
        return new OpenAPI()
                .addServersItem(new Server().url(api))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info().title("Innowise intern project"));
    }


}





