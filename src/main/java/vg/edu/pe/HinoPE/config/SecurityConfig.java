package vg.edu.pe.HinoPE.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import vg.edu.pe.HinoPE.security.JwtAuthenticationFilter;
import vg.edu.pe.HinoPE.security.SecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityContextRepository securityContextRepository;
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF for REST API
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                
                // Disable form login
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                
                // Disable HTTP Basic
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                
                // Configure authorization
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints (no authentication required)
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers("/api/public/**").permitAll()
                        
                        // Swagger/OpenAPI endpoints
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        
                        // All other /api/** endpoints require authentication
                        .pathMatchers("/api/**").authenticated()
                        
                        // Allow all other requests
                        .anyExchange().permitAll()
                )
                
                // Use custom security context repository
                .securityContextRepository(securityContextRepository)
                
                // Add JWT authentication filter
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                
                .build();
    }
}
