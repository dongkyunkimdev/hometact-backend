package kdk.hometact.user.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import kdk.hometact.user.EnumAuthority;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authority")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

	@Id
	@Column(name = "authority_name", length = 50)
	private String authorityName;

	@Builder
	public Authority(String authorityName) {
		this.authorityName = authorityName;
	}

	public static Authority createUserRole() {
		return Authority.builder()
			.authorityName(EnumAuthority.ROLE_USER.name())
			.build();
	}

	public static Authority createAdminRole() {
		return Authority.builder()
			.authorityName(EnumAuthority.ROLE_ADMIN.name())
			.build();
	}
}