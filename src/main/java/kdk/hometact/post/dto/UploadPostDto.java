package kdk.hometact.post.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadPostDto {

	@NotNull
	@Size(max = 500)
	private String title;

	@NotNull
	@Size(max = 5000)
	private String content;

	@NotNull
	private Long postCategoryId;

	@Builder
	public UploadPostDto(
		@NotNull @Size(max = 500) String title,
		@NotNull @Size(max = 5000) String content, Long postCategoryId) {
		this.title = title;
		this.content = content;
		this.postCategoryId = postCategoryId;
	}
}
