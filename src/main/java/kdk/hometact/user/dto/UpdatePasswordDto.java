package kdk.hometact.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePasswordDto {

	@NotNull
	@Size(min = 8, max = 100)
	private String originalPassword;

	@NotNull
	@Size(min = 8, max = 100)
	private String newPassword;

	@Builder
	public UpdatePasswordDto(
		@NotNull @Size(min = 8, max = 100) String originalPassword,
		@NotNull @Size(min = 8, max = 100) String newPassword) {
		this.originalPassword = originalPassword;
		this.newPassword = newPassword;
	}
}
