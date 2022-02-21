package kdk.hometact.comment.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCommentDto {

	@NotNull
	@Size(max = 5000)
	private String content;

	@Builder
	public UpdateCommentDto(
		@NotNull @Size(max = 5000) String content) {
		this.content = content;
	}
}
