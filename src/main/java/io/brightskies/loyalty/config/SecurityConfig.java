package io.brightskies.loyalty.config;

import io.brightskies.loyalty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    // beans
    // bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // authenticationProvider bean definition

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * http.cors();
         * http.csrf().disable();
         */
        http
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/api/v1/me").hasRole("USER")
                .requestMatchers("/api/v1/users/**").hasRole("USER")
                .requestMatchers("/products/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/generator/**").permitAll()
                .requestMatchers("/orders/**").authenticated()
                .requestMatchers("/error").permitAll()
        );
        http
                .csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }
    /*
     * @Bean
     * CorsConfigurationSource corsConfigurationSource() {
     * CorsConfiguration configuration = new CorsConfiguration();
     * configuration.setAllowedOrigins(List.of("*"));
     * configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE"));
     * configuration.addAllowedHeader("*");
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", configuration);
     * return source;
     * }
     */

}
