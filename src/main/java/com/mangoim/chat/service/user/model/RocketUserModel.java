package com.mangoim.chat.service.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mangoim.chat.service.configuration.jackson.ZonedDateTimeDeserializer;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RocketUserModel implements Serializable {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Email {
        private String address;
        private Boolean verified;
    }

    @JsonProperty("_id")
    private String id;

    private String username;

    private String name;

    private String type;

    private List<Email> emails;

    private String offline;

    private Boolean active;

    private List<String> roles;

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime createdAt;

    private String status;

    private String statusConnection;

    private String avatarUrl;

    @JsonProperty("_updatedAt")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime updatedAt;

    private Map<String, Object> settings;
}
