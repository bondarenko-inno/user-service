package org.ebndrnk.userservice.config;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.userservice.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for setting up Spring Security.
 * Enables stateless security with JWT authentication, disables CSRF, and configures Swagger to be publicly accessible.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * List of URL patterns that are publicly accessible (no authentication required).
     * Mainly used for allowing Swagger UI and API docs.
     */
    private static final String[] WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };

    /**
     * Defines the security filter chain for the application.
     * <ul>
     *     <li>Disables CSRF protection (as we use JWT and stateless sessions).</li>
     *     <li>Enables CORS with default configuration.</li>
     *     <li>Allows unauthenticated access to whitelisted endpoints (Swagger docs).</li>
     *     <li>Secures all other endpoints by requiring authentication.</li>
     *     <li>Sets session management to stateless (no HTTP session storage).</li>
     *     <li>Adds a custom JWT authentication filter before the default Spring authentication filter.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
