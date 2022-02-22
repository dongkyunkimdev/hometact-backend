package kdk.hometact.security.jwt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {

	private String accessToken;
	private String refreshToken;
	private String nickname;

	@Builder
	public TokenDto(String accessToken, String refreshToken, String nickname) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.nickname = nickname;
	}
}
