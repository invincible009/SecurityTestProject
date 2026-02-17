package com.sdl.config;

import com.sdl.exceptions.SecurityAppAccessDeniedHandler;
import com.sdl.exceptions.SecurityAppBasicAuthenticationEntryPoint;
import com.sdl.filter.JWTTokenGeneratorFilter;
import com.sdl.filter.JWTTokenValidatorFilter;
import com.sdl.filter.RequestValidationBeforeFilter;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
public class SecurityConfiguration{

    @Bean
    SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http, Environment environment) throws Exception {

        http
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore(new RequestValidationBeforeFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(environment), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/public/**",
                                "/api/onboarding/register",
                                "/api/onboarding/login"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/update/**").hasRole("USER")
                        .requestMatchers("/api/user/me").authenticated()
                        .anyRequest().authenticated()
                );

        http.httpBasic(AbstractHttpConfigurer::disable);
        http.exceptionHandling(htc -> htc
                .authenticationEntryPoint(new SecurityAppBasicAuthenticationEntryPoint())
                .accessDeniedHandler(new SecurityAppAccessDeniedHandler()));
        http.formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();

    }

}
