package kdk.hometact.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import kdk.hometact.postcategory.dto.PostCategoryDto;
import kdk.hometact.postlike.dto.PostLikeDto;
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
	private List<PostLikeDto> postLikeDtos = new ArrayList<>();

	@JsonProperty(access = Access.READ_ONLY)
	private List<CommentDto> commentDtos = new ArrayList<>();

	@JsonProperty(access = Access.READ_ONLY)
	private PostCategoryDto postCategoryDto;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDateTime modifiedDate;

	@Builder
	public PostDto(Long postId, UserDto userDto,
		@NotNull @Size(max = 500) String title,
		@NotNull @Size(max = 5000) String content, Long view,
		List<PostLikeDto> postLikeDtos, List<CommentDto> commentDtos,
		PostCategoryDto postCategoryDto, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.postId = postId;
		this.userDto = userDto;
		this.title = title;
		this.content = content;
		this.view = view;
		this.postLikeDtos = postLikeDtos;
		this.commentDtos = commentDtos;
		this.postCategoryDto = postCategoryDto;
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
			.postLikeDtos(
				post.getPostLikes().stream().map(
					postLike -> PostLikeDto.from(postLike)
				)
					.collect(Collectors.toList())
			)
			.commentDtos(
				post.getComments().stream().map(
					comment -> CommentDto.from(comment)
				)
					.collect(Collectors.toList()))
			.postCategoryDto(PostCategoryDto.from(post.getPostCategory()))
			.createdDate(post.getCreatedDate())
			.modifiedDate(post.getModifiedDate())
			.build();
	}

}
