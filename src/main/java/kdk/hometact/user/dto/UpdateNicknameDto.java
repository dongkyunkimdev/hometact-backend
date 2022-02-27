package kdk.hometact.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateNicknameDto {

	@NotNull
	@Size(min = 3, max = 20)
	private String nickname;

	@Builder
	public UpdateNicknameDto(
		@NotNull @Size(min = 3, max = 20) String nickname) {
		this.nickname = nickname;
	}
}
