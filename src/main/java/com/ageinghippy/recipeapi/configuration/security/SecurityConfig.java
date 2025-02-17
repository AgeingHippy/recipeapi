package com.ageinghippy.recipeapi.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // disable CSRF for Postman usage
                .csrf(AbstractHttpConfigurer::disable)
                // permit all requests to access CSS and JavaScript
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css", "/js").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // allow all requests to read recipes and reviews
                        .requestMatchers(HttpMethod.GET, "/recipe/**", "/review/**", "/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        // allow creation of new recipes and reviews
//                        .requestMatchers(HttpMethod.POST, "/recipes", "/reviews", "/user").permitAll()
                        // all other requests should be authenticated
                        .anyRequest().authenticated())
                // users should log in with HTTP Basic Authentication.
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

