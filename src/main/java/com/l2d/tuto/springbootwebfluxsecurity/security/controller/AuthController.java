package com.l2d.tuto.springbootwebfluxsecurity.security.controller;

import com.l2d.tuto.springbootwebfluxsecurity.security.AuthService;
import com.l2d.tuto.springbootwebfluxsecurity.security.dto.CreateUserVM;
import com.l2d.tuto.springbootwebfluxsecurity.security.dto.LoginVM;
import com.l2d.tuto.springbootwebfluxsecurity.security.dto.UserDetailsVM;
import com.l2d.tuto.springbootwebfluxsecurity.user.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 *
 * @author duc-d
 *
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
       this.authService = authService;
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public Mono<UserDetailsVM> signin(@Valid @RequestBody LoginVM loginVM) {
        return authService.signin(loginVM);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public Mono<UserModel> signup(@Valid @RequestBody CreateUserVM createUserVM) {
        return authService.signup(createUserVM);
    }
}
