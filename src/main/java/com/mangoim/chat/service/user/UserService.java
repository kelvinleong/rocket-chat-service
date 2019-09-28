package com.mangoim.chat.service.user;

import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.security.exception.InternalException;
import com.mangoim.chat.service.user.domain.User;
import com.mangoim.chat.service.user.model.CreateRocketUserModel;
import com.mangoim.chat.service.user.model.RocketLoginResponse;
import com.mangoim.chat.service.user.model.RocketUserResponse;
import com.mangoim.chat.service.user.model.UserModel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {
    private final WebClient client;
    private final String adminUsername;
    private final String adminPassword;
    private Map<String, String> adminCredential;

    private final String X_AUTH_TOKEN = "X-Auth-Token";
    private final String X_USER_ID = "X-User-Id";

    public UserService(@Value("${rocket.server.url}") String rocketUrl,
                       @Value("${rocket.admin.username}") String username,
                       @Value("${rocket.admin.password}") String password) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        //ClientHttpConnector httpConnector = new ReactorClientHttpConnector(opt -> opt.sslContext(sslContext));
        var httpConnector = HttpClient.create().secure(t -> t.sslContext(sslContext));
        client = WebClient.builder().baseUrl(rocketUrl).clientConnector(new ReactorClientHttpConnector(httpConnector)).build();
        adminUsername = username;
        adminPassword = password;
        adminCredential = new ConcurrentHashMap<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        LoginVM admin = LoginVM.builder().user(adminUsername).password(adminPassword).build();
        login(admin, "")
            .subscribe(
                    this::updateAdminCredential,
                    error -> {
                        log.error("Failed to login via admin", error);
                        throw new InternalException(error.getMessage());
                    }
            );
    }

    private void updateAdminCredential(UserDetailsVM admin) {
        log.info("update admin credential");
        adminCredential.putIfAbsent(X_USER_ID, admin.getRocketId());
        adminCredential.putIfAbsent(X_AUTH_TOKEN, admin.getAuthToken());
    }

    public Mono<UserDetailsVM> login(LoginVM user, String token) {
        return client.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(user))
                .retrieve()
                .onStatus(e -> e.is4xxClientError() || e.is5xxServerError(), resp -> {
                    log.error("Failed to login due to error: {}, msg: {}", resp.statusCode().value(),
                                                            resp.statusCode().getReasonPhrase());
                    return Mono.error(new InternalException(resp.statusCode().getReasonPhrase()));
                })
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
                      .header(X_AUTH_TOKEN, adminCredential.get(X_AUTH_TOKEN))
                      .header(X_USER_ID, adminCredential.get(X_USER_ID))
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept( MediaType.APPLICATION_JSON )
                      .body(BodyInserters.fromObject(model))
                      .retrieve()
                      .onStatus(e -> e.is4xxClientError() || e.is5xxServerError(), resp -> {
                          log.error("Faield to create rocket user due to error: {}, msg: {}", resp.statusCode().value(),
                                                                    resp.statusCode().getReasonPhrase());
                          return Mono.error(new InternalException(resp.statusCode().getReasonPhrase()));
                      })
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
