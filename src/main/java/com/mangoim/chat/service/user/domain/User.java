package com.mangoim.chat.service.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String username;

    private String displayName;

    private String email;

    @Size(min = 60, max = 60)
    private String password;

    @CreatedDate
    private ZonedDateTime createdAt;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();
}
