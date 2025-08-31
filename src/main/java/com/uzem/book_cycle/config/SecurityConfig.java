package com.uzem.book_cycle.config;

import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.jwt.JwtAccessDeniedHandler;
import com.uzem.book_cycle.security.jwt.JwtAuthenticationEntryPoint;
import com.uzem.book_cycle.security.jwt.JwtFilter;
import com.uzem.book_cycle.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisUtil redisUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore(new JwtFilter(tokenProvider, redisUtil),
                        UsernamePasswordAuthenticationFilter.class) // JwtFilter 직접 등록
                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(jwtAccessDeniedHandler);
                    exception.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                })

                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .securityContext(securityContext
                        -> securityContext.requireExplicitSave(false))

                .authorizeHttpRequests(authorizeHttpRequests
                        -> authorizeHttpRequests
                        .requestMatchers("/api/v2/admin/**").hasRole("ADMIN") // ROLE_ 자동 추가됨
                        .requestMatchers("/auth/**").permitAll() // 로그인, 회원가입은 열어주기
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/rentals/**").permitAll()
                        .requestMatchers("/books/**").permitAll()
                        .requestMatchers("/stomp-test.html").permitAll()
                        .requestMatchers("/ws-stomp/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 🔥 Cors 설정 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://localhost:5173"); // 프론트 주소
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // 필요 시 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

