package com.mangoim.chat.service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RocketCredential {
    private String authToken;
    private String userId;
    private RocketUserModel me;
}
