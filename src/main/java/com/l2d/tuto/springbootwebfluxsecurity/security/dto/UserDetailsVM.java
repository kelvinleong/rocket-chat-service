package com.l2d.tuto.springbootwebfluxsecurity.security.dto;

import com.l2d.tuto.springbootwebfluxsecurity.user.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsVm {
    private String id;
    private String username;
    private String rocketId;
    private String jwtToken;
    private String authToken;
    private Authority role;
    private String avatarUrl;
}
