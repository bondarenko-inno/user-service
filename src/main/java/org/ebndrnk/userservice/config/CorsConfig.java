package org.ebndrnk.userservice.config;

import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for enabling Cross-Origin Resource Sharing (CORS) settings.
 * This allows configuring allowed origins, HTTP methods, and headers for cross-origin requests.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a {@link WebMvcConfigurer} bean that configures CORS mappings.
     *
     * @return a WebMvcConfigurer that allows CORS requests from any origin,
     *         supporting GET, POST, PUT, DELETE and OPTIONS HTTP methods with all headers allowed.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configure CORS mappings.
             *
             * @param registry the {@link CorsRegistry} to add mappings to; will not be null
             */
            @Override
            public void addCorsMappings(@Nullable CorsRegistry registry) {
                assert registry != null;
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
