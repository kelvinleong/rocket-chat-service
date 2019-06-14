package com.l2d.tuto.springbootwebfluxsecurity.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserVM {
    private String username;
    private String displayName;
    private String email;
    private String password;
}
