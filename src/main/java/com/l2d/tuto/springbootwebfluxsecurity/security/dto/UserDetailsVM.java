package com.l2d.tuto.springbootwebfluxsecurity.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsVM {
    private String username;
    private String rocketId;
    private String jwtToken;
    private String authToken;
    private List<String> roles;
    private String avatarUrl;
}
