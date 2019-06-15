package com.mangoim.chat.service.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRocketUserModel {
    private String username;
    private String password;
    private String name;
    private String email;
}
