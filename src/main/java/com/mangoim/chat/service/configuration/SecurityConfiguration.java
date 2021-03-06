package com.mangoim.chat.service.configuration;

import com.mangoim.chat.service.ApiVersion;
import com.mangoim.chat.service.audit.AuditLogConfig;
import com.mangoim.chat.service.security.AuthoritiesConstants;
import com.mangoim.chat.service.security.ReactiveUserDetailsServiceImpl;
import com.mangoim.chat.service.security.TokenAuthenticationConverter;
import com.mangoim.chat.service.security.UnauthorizedAuthenticationEntryPoint;
import com.mangoim.chat.service.security.jwt.JWTHeadersExchangeMatcher;
import com.mangoim.chat.service.security.jwt.JWTReactiveAuthenticationManager;
import com.mangoim.chat.service.security.jwt.TokenProvider;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private final ReactiveUserDetailsServiceImpl reactiveUserDetailsService;
    private final TokenProvider tokenProvider;

    private static final String[] AUTH_WHITELIST = {
            "/resources/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/**/api-docs",
            "/**/springfox-swagger-ui/**",
            "/favicon.ico",
            ApiVersion.V1 + "/auth/signin",
            ApiVersion.V1 + "/auth/signup"
    };

    public SecurityConfiguration(ReactiveUserDetailsServiceImpl reactiveUserDetailsService,
                                 TokenProvider tokenProvider) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, UnauthorizedAuthenticationEntryPoint entryPoint) {

        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();

        http
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .authorizeExchange()
                .pathMatchers(AUTH_WHITELIST).permitAll()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .and()
                .authorizeExchange()
                .matchers(EndpointRequest.toAnyEndpoint())
                .hasAuthority(AuthoritiesConstants.ADMIN)
                .and()
                .addFilterAt(auditLogFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(webFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange();

        return http.build();
    }

    @Bean
    public WebFilter auditLogFilter() {
        return new AuditLogConfig();
    }

    @Bean
    public AuthenticationWebFilter webFilter() {
        var authenticationWebFilter = new AuthenticationWebFilter(repositoryReactiveAuthenticationManager());
        authenticationWebFilter.setAuthenticationConverter(new TokenAuthenticationConverter(tokenProvider));
        authenticationWebFilter.setRequiresAuthenticationMatcher(new JWTHeadersExchangeMatcher());
        authenticationWebFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        return authenticationWebFilter;
    }

    @Bean
    public JWTReactiveAuthenticationManager repositoryReactiveAuthenticationManager() {
        return new JWTReactiveAuthenticationManager(reactiveUserDetailsService, passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
