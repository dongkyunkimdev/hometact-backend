package kdk.hometact.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.post.Post;
import kdk.hometact.user.dto.UserDto;
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
	private UserDto userDto;

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
	private List<CommentDto> commentDto = new ArrayList<>();

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime modifiedDate;

	@Builder
	public PostDto(Long postId, UserDto userDto,
		@NotNull @Size(max = 500) String title,
		@NotNull @Size(max = 5000) String content, Long view, Long like,
		List<CommentDto> commentDto,
		LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.postId = postId;
		this.userDto = userDto;
		this.title = title;
		this.content = content;
		this.view = view;
		this.like = like;
		this.commentDto = commentDto;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static PostDto from(Post post) {
		return PostDto.builder()
			.postId(post.getPostId())
			.userDto(UserDto.from(post.getUser()))
			.title(post.getTitle())
			.content(post.getContent())
			.view(post.getView())
			.like(Long.valueOf(post.getPostLikes().size()))
			.commentDto(
				post.getComments().stream().map(
					comment -> CommentDto.from(comment)
				)
					.collect(Collectors.toList()))
			.createdDate(post.getCreatedDate())
			.modifiedDate(post.getModifiedDate())
			.build();
	}

}
