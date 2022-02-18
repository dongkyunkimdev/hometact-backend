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
	@Size(max = 500)
	private String title;

	@NotNull
	@Size(max = 5000)
	private String content;

	@JsonProperty(access = Access.READ_ONLY)
	private Long view;

	@JsonProperty(access = Access.READ_ONLY)
	private Long like;

	@JsonProperty(access = Access.READ_ONLY)
	private Long comment;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime modifiedDate;

	@Builder
	public PostDto(Long postId, Long userId,
		@NotNull @Size(max = 500) String title,
		@NotNull @Size(max = 5000) String content, Long view, Long like, Long comment,
		LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.postId = postId;
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.view = view;
		this.like = like;
		this.comment = comment;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static PostDto from(Post post) {
		return PostDto.builder()
			.postId(post.getPostId())
			.userId(post.getUser().getUserId())
			.title(post.getTitle())
			.content(post.getContent())
			.view(post.getView())
			.like(Long.valueOf(post.getPostLikes().size()))
			.comment(Long.valueOf(post.getComments().size()))
			.createdDate(post.getCreatedDate())
			.modifiedDate(post.getModifiedDate())
			.build();
	}
}
