package com.windfall.global.config.security;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationFilter customAuthenticationFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers
            .addHeaderWriter(new XFrameOptionsHeaderWriter(
                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/","/error", "/favicon.ico").permitAll()
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
            .requestMatchers("/ws-stomp/**", "/ws-stomp-public/**").permitAll()
            .requestMatchers("/api/v1/auctions/*/history", "/api/v1/auctions/*").permitAll()
            .requestMatchers("/api/v1/auctions/*/seller").permitAll()
            .requestMatchers("/api/v1/tags/search").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/auctions", "/api/v1/auctions/search").permitAll()
            .requestMatchers("/api/v1/payments/**").authenticated()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // TODO: 개발 중이므로 나머지는 허용
            .anyRequest().permitAll()
        )

        .exceptionHandling(handling -> handling
            .authenticationEntryPoint((request, response, ex) -> {
              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write("""
                  {"resultCode":"UNAUTHORIZED","msg":"인증 정보가 없거나 만료되었습니다."}
                  """);
            })
        )
        .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
