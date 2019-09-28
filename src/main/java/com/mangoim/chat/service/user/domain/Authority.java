package com.mangoim.chat.service.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author duc-d
 * An authority (a security role) used by Spring Security.
 */
@Document(collection = "authority")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Authority implements Serializable, GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Indexed(unique = true)
	private String name;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Authority authority = (Authority) o;

		return !(name != null ? !name.equals(authority.name) : authority.name != null);
	}

	@Override
	public String getAuthority() {
		return name;
	}
}
