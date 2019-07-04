package com.mangoim.chat.service.user;

import com.mangoim.chat.service.security.dto.CreateUserVM;
import com.mangoim.chat.service.security.dto.LoginVM;
import com.mangoim.chat.service.security.dto.UserDetailsVM;
import com.mangoim.chat.service.security.exception.InternalException;
import com.mangoim.chat.service.user.domain.User;
import com.mangoim.chat.service.user.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class userServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private WebClient webclient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(webclient, "admin", "adminpwd");
    }

    private LoginVM userCredential() {
        return LoginVM.builder().user("username").password("pass").build();
    }

    private RocketUserModel rocketUserModel() {
        return RocketUserModel.builder()
                .id("id")
                .name("name")
                .username("username")
                .active(true)
                .avatarUrl("url")
                .createdAt(ZonedDateTime.now())
                .emails(Arrays.asList(RocketUserModel.Email.builder()
                        .address("email@email.com")
                        .verified(true)
                        .build()))
                .roles(Arrays.asList("admin"))
                .type("type")
                .updatedAt(ZonedDateTime.now())
                .build();

    }

    private RocketLoginResponse rocketLoginResponse() {
        var me = rocketUserModel();

        var data = RocketCredential.builder()
                .authToken("authToken")
                .userId("rocketUserId")
                .me(me)
                .build();

        return RocketLoginResponse.builder()
                .status("online")
                .data(data )
                .build();
    }

    private CreateUserVM createUser() {
        return CreateUserVM.builder()
                .username("username")
                .email("email")
                .displayName("displayName")
                .password("password")
                .build();
    }

    private User user() {
        return User.builder()
                .id("id")
                .username("username")
                .displayName("displayName")
                .email("email")
                .password("password")
                .createdAt(ZonedDateTime.now())
                .build();
    }

    private RocketUserResponse rocketUserResponse() {
        return RocketUserResponse.builder().success(true).user(rocketUserModel()).build();
    }

    @Test
    public void shouldLoginWithAdmin() {
        final var postUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        final var bodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        final var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        when(webclient.post()).thenReturn(postUriSpecMock);
        when(postUriSpecMock.uri(anyString())).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.accept(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(),  any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<RocketLoginResponse>>notNull()))
                .thenReturn(Mono.just(rocketLoginResponse()));

        Mono<UserDetailsVM> result = userService.login(userCredential(), "token");

        UserDetailsVM user = result.block();
        assertThat(user).isNotNull();
        assertThat(user.getAuthToken()).isEqualTo("authToken");
        assertThat(user.getRocketId()).isEqualTo("rocketUserId");
        assertThat(user.getJwtToken()).isEqualTo("token");
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getAvatarUrl()).isEqualTo("url");
    }

    @Test(expected = InternalException.class)
    public void shouldFailedLoginWithAdmin() {
        final var postUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        final var bodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        final var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        when(webclient.post()).thenReturn(postUriSpecMock);
        when(postUriSpecMock.uri(anyString())).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.accept(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(),  any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<RocketLoginResponse>>notNull()))
                .thenReturn(Mono.error(new InternalException("Rocket Error")));

        Mono<UserDetailsVM> result = userService.login(userCredential(), "token");
        result.block();
    }

    @Test
    public void shouldCreateRocketUser() {
        var createUserVM = createUser();
        final var postUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        final var bodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        final var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        when(webclient.post()).thenReturn(postUriSpecMock);
        when(postUriSpecMock.uri(anyString())).thenReturn(bodySpecMock);
        when(bodySpecMock.header(any(), any())).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.accept(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(),  any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<RocketUserResponse>>notNull()))
                .thenReturn(Mono.just(rocketUserResponse()));

        Mono<UserModel> result = userService.createRocketChatUser(createUserVM, user());
        UserModel user = result.block();

        assertThat(user.getUsername()).isEqualTo(createUserVM.getUsername());
        assertThat(user.getDisplayName()).isEqualTo(createUserVM.getDisplayName());
        assertThat(user.getEmail()).isEqualTo(createUserVM.getEmail());
        assertThat(user.getRocketId()).isNotEmpty();
    }


    @Test(expected = InternalException.class)
    public void shouldFailedCreateRocketUser() {
        final var postUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        final var bodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        final var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        when(webclient.post()).thenReturn(postUriSpecMock);
        when(postUriSpecMock.uri(anyString())).thenReturn(bodySpecMock);
        when(bodySpecMock.header(any(), any())).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.accept(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(),  any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<RocketUserResponse>>notNull()))
                .thenReturn(Mono.error(new InternalException("Rocket Error")));

        Mono<UserModel> result = userService.createRocketChatUser(createUser(), user());
        result.block();
    }
}
