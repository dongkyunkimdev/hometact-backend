package kdk.hometact.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.hometact.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long postId;

	@JsonProperty(access = Access.READ_ONLY)
	private Long userId;

	@NotNull
	@Size(max = 100)
	private String title;

	@NotNull
	@Size(max = 500)
	private String content;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime modifiedDate;

	@Builder
	public PostDto(Long postId, Long userId,
		@NotNull @Size(max = 100) String title,
		@NotNull @Size(max = 500) String content, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.postId = postId;
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static PostDto from(Post post) {
		return PostDto.builder()
			.postId(post.getPostId())
			.userId(post.getUser().getUserId())
			.title(post.getTitle())
			.content(post.getContent())
			.createdDate(post.getCreatedDate())
			.modifiedDate(post.getModifiedDate())
			.build();
	}
}
