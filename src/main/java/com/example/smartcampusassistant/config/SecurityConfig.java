package com.example.smartcampusassistant.config;

import com.example.smartcampusassistant.security.CustomUserDetailsService;
import com.example.smartcampusassistant.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use allowedOriginPatterns instead of allowedOrigins when allowCredentials is true
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://10.180.43.206:*",
                "http://13.200.246.254:*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public HTML pages
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/student-dashboard.html").permitAll()
                        .requestMatchers("/faculty-dashboard.html").permitAll()
                        .requestMatchers("/admin-dashboard.html").permitAll()
                        .requestMatchers("/feedback.html").permitAll()
                        .requestMatchers("/hostel.html").permitAll()
                        .requestMatchers("/placement.html").permitAll()
                        .requestMatchers("/**/*.html").permitAll()
                        .requestMatchers("/**/*.css").permitAll()
                        .requestMatchers("/**/*.js").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/favicon.ico", "/webjars/**").permitAll()

                        // ✅ Public API endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/registrations/verify").permitAll()
                        .requestMatchers("/api/registrations/public/**").permitAll()
                        .requestMatchers("/api/certificates/view/**").permitAll()
                        .requestMatchers("/api/certificates/base64/**").permitAll()
                        .requestMatchers("/api/leaves/types").permitAll()
                        .requestMatchers("/api/notices").permitAll()
                        .requestMatchers("/api/library/books").permitAll()
                        .requestMatchers("/api/complaints/faculty").permitAll()
                        .requestMatchers("/api/course-material/**").permitAll()
                        .requestMatchers("/api/assignments/**").permitAll()
                        .requestMatchers("/api/transport/**").permitAll()
                        .requestMatchers("/api/attendance/**").permitAll()
                        .requestMatchers("/api/cafeteria/**").permitAll()
                        .requestMatchers("/api/events/**").permitAll()
                        .requestMatchers("/api/clubs/**").permitAll()
                        .requestMatchers("/api/users/students").permitAll()
                        .requestMatchers("/api/users/faculty").permitAll()
                        .requestMatchers("/api/feedback/**").permitAll()
                        .requestMatchers("/api/hostel/**").permitAll()
                        .requestMatchers("/api/placement/**").permitAll()
                        .requestMatchers("/api/chat/health").permitAll()
                        .requestMatchers("/api/chat/topics").permitAll()
                        // ✅ Students can view their own leaves and apply for new ones
                        .requestMatchers(HttpMethod.GET,  "/api/leaves/user/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/leaves").authenticated()
                        // ✅ Admin / faculty can see all leaves, pending list, approve, reject, etc.
                        .requestMatchers("/api/leaves/**").hasAnyRole("ADMIN", "FACULTY")
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ✅ All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}