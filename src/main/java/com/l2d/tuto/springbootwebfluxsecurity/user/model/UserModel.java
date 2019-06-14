package com.l2d.tuto.springbootwebfluxsecurity.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String id;
    private String rocketId;
    private String username;
    private String displayName;
    private String email;
}
