package com.mangoim.chat.service.security;

import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.security.exception.InternalException;
import com.mangoim.chat.service.security.exception.UnauthorizedException;
import com.mangoim.chat.service.security.jwt.JWTReactiveAuthenticationManager;
import com.mangoim.chat.service.security.jwt.TokenProvider;
import com.mangoim.chat.service.user.UserService;
import com.mangoim.chat.service.user.domain.Authority;
import com.mangoim.chat.service.user.domain.User;
import com.mangoim.chat.service.user.model.UserModel;
import com.mangoim.chat.service.user.repository.AuthorityRepository;
import com.mangoim.chat.service.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.ZonedDateTime;
import java.util.Set;

@Slf4j
@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final JWTReactiveAuthenticationManager authenticationManager;
    private final Validator validation;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(TokenProvider tokenProvider, JWTReactiveAuthenticationManager authenticationManager,
                       Validator validation, UserRepository userRepository, AuthorityRepository authorityRepository,
                       PasswordEncoder passwordEncoder, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.validation = validation;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    /***
     *  init user roles at the first deployment
     */
    public void init() {
        Authority admin = Authority.builder().name(AuthoritiesConstants.ADMIN).build();
        authorityRepository.save(admin).subscribe(s -> log.info("{}::{}", s.getId(), s.getName()),
                                                err -> log.error("", err));

        Authority user = Authority.builder().name(AuthoritiesConstants.USER).build();
        authorityRepository.save(user).subscribe(s -> log.info("{}::{}", s.getId(), s.getName()),
                                                err -> log.error("", err));
    }

    public Mono<UserDetailsVM> signin(@Valid @RequestBody LoginVM loginVM) {
        if (!this.validation.validate(loginVM).isEmpty()) {
            return Mono.error(new RuntimeException("Bad request"));
        }

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUser(), loginVM.getPassword());

        return authenticationManager.authenticate(authenticationToken)
                .doOnError(throwable -> {
                    throw new UnauthorizedException("Bad crendentials");
                })
                .doOnSuccess(ReactiveSecurityContextHolder::withAuthentication)
                .flatMap(authentication -> Mono.just(tokenProvider.createToken(authentication)))
                .flatMap(token -> userService.login(loginVM, token)
                            .doOnError(throwable -> {
                                log.error("Failed to login to rocket, {}", loginVM.getUser(), throwable);
                                throw new UnauthorizedException("Bad credential on rocket");
                            })
                );
    }

    public Mono<UserModel> signup(@Valid @RequestBody CreateUserVM createUserVM) {
        return  authorityRepository
                   .findByName(AuthoritiesConstants.USER)
                   .doOnSuccess(r -> {
                       if(r == null) {
                           log.error("Role is not found....");
                           throw new UnsupportedOperationException("Role is not found");
                       }
                   })
                   .flatMap(role -> userRepository
                                .save(User.builder()
                                       .username(createUserVM.getUsername())
                                       .displayName(createUserVM.getDisplayName())
                                       .email(createUserVM.getEmail())
                                       .password(passwordEncoder.encode(createUserVM.getPassword()))
                                       .createdAt(ZonedDateTime.now())
                                       .authorities(Set.of(role))
                                       .build())
                                .doOnError(throwable -> {
                                    log.error("Failed to persist user, {}", createUserVM.getUsername(), throwable);
                                    throw new InternalException("Something went wrong on db");
                                })
                   )
                   .flatMap(user -> userService
                          .createRocketChatUser(createUserVM, user)
                          .doOnError(throwable -> {
                              log.error("Failed to create rocket user, {}", createUserVM.getUsername(), throwable);
                              throw new InternalException("Something went wrong on rocket");
                          })
                   );
    }
}
