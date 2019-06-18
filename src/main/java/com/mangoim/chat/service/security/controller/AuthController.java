package com.mangoim.chat.service.security.controller;

import com.mangoim.chat.service.ApiVersion;
import com.mangoim.chat.service.security.AuthService;
import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.user.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(ApiVersion.V1 + "/auth")
@Api(tags = "Authentication")
@Slf4j
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
       this.authService = authService;
    }

    @ApiOperation(value = "User login",
            nickname = "User login",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserDetailsVM.class),
            @ApiResponse(code = 401, message = "UnAuthorized"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Mono<UserDetailsVM> signin(@Valid @RequestBody LoginVM loginVM) {
        return authService.signin(loginVM);
    }

    @ApiOperation(value = "User signup",
            nickname = "User signup",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = UserModel.class),
            @ApiResponse(code = 422, message = "UnsupportedOperations"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Mono<UserModel> signup(@Valid @RequestBody CreateUserVM createUserVM) {
        return authService.signup(createUserVM);
    }
}
