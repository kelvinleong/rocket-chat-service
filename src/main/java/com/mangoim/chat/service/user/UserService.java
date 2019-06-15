package com.mangoim.chat.service.user;

import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.user.domain.User;
import com.mangoim.chat.service.user.model.CreateRocketUserModel;
import com.mangoim.chat.service.user.model.RocketLoginResponse;
import com.mangoim.chat.service.user.model.RocketUserResponse;
import com.mangoim.chat.service.user.model.UserModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private WebClient client = WebClient.create("http://chat.inumio.com:4000");

    public Mono<UserDetailsVM> login(LoginVM user, String token) {
        return client.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept( MediaType.APPLICATION_JSON )
                .body(BodyInserters.fromObject(user))
                .retrieve()
                .bodyToMono(RocketLoginResponse.class)
                .flatMap(login ->  Mono.just(UserDetailsVM.builder()
                                    .username(user.getUser())
                                    .authToken(login.getData().getAuthToken())
                                    .rocketId(login.getData().getUserId())
                                    .avatarUrl(login.getData().getMe().getAvatarUrl())
                                    .roles(login.getData().getMe().getRoles())
                                    .jwtToken(token)
                                    .build())
                );
    }

    public Mono<UserModel> createRocketChatUser(CreateUserVM userModel, User user) {
        CreateRocketUserModel model = CreateRocketUserModel.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getDisplayName())
                .password(userModel.getPassword())
                .build();

        return client.post()
                      .uri("/api/v1/users.create")
                      .header("X-Auth-Token", "UQJKSjRGqw2KSRQkLO-Fsn3oNYoL6YtYSNV45RAVrKD")
                      .header("X-User-Id", "egrWRCWhwERuNNDB3")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept( MediaType.APPLICATION_JSON )
                      .body(BodyInserters.fromObject(model))
                      .retrieve()
                      .bodyToMono(RocketUserResponse.class)
                      .flatMap(r -> Mono.just(UserModel.builder()
                                        .id(user.getId())
                                        .rocketId(r.getUser().getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .displayName(user.getDisplayName())
                                        .build()));
    }
}
