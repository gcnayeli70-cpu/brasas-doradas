package com.brasasdoradas.sistema.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    private static final String ROL_CAJERO = "CAJERO";
    private static final String ROL_MOZO = "MOZO";

    private static final String CSP_POLICY =
            "default-src 'self'; "
                    + "script-src 'self' https://cdn.jsdelivr.net; "
                    + "style-src 'self' https://cdn.jsdelivr.net; "
                    + "font-src 'self' https://cdn.jsdelivr.net; "
                    + "img-src 'self' data: https://api.qrserver.com; "
                    + "object-src 'none'; "
                    + "form-action 'self'; "
                    + "frame-ancestors 'self'";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/clientes/**", "/mesas/**").hasAnyRole(ROL_ADMINISTRADOR, ROL_CAJERO, ROL_MOZO)
                        .requestMatchers("/productos/**", "/reportes/**").hasAnyRole(ROL_ADMINISTRADOR, ROL_CAJERO)
                        .requestMatchers("/pedidos/**").hasAnyRole(ROL_ADMINISTRADOR, ROL_CAJERO, ROL_MOZO)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .headers(headers -> headers
                        .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", CSP_POLICY))
                );

        return http.build();
    }
}