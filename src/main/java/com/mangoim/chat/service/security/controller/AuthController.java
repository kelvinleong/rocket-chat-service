package com.mangoim.chat.service.security.controller;

import com.mangoim.chat.service.security.AuthService;
import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.user.model.UserModel;
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
