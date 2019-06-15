package com.mangoim.chat.service.security.jwt;

import com.mangoim.chat.service.security.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public JWTReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return Mono.just(authentication)
                .switchIfEmpty(Mono.defer(this::raiseUnauthenticatedException))
                .cast(UsernamePasswordAuthenticationToken.class)
                .flatMap(this::authenticateToken)
                .publishOn(Schedulers.parallel())
                .onErrorResume(e -> {
                    log.error("something goes wrong", e);
                    return raiseUnauthenticatedException();
                })
                .filter(u -> passwordEncoder.matches((String) authentication.getCredentials(), u.getPassword()))
                .switchIfEmpty(Mono.defer(this::raiseUnauthenticatedException))
                .map(u -> new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), u.getAuthorities()));
    }

    private <T> Mono<T> raiseUnauthenticatedException() {
        return Mono.error(new UnauthorizedException("Invalid Credentials"));
    }

    private Mono<UserDetails> authenticateToken(final UsernamePasswordAuthenticationToken authenticationToken) {
        String username = authenticationToken.getName();

        log.info("checking authentication for user " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("authenticated user [" + username + "], setting security context");
            return userDetailsService.findByUsername(username);
        }

        return null;
    }
}
