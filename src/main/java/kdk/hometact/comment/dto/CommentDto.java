package kdk.hometact.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.hometact.comment.Comment;
import kdk.hometact.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long commentId;

	@NotNull
	private Long postId;

	@NotNull
	@Size(max = 5000)
	private String content;

	@JsonProperty(access = Access.READ_ONLY)
	private UserDto userDto;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime modifiedDate;

	@Builder
	public CommentDto(Long commentId, @NotNull Long postId,
		@NotNull @Size(max = 500) String content, UserDto userDto, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.commentId = commentId;
		this.postId = postId;
		this.content = content;
		this.userDto = userDto;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static CommentDto from(Comment comment) {
		return CommentDto.builder()
			.commentId(comment.getCommentId())
			.postId(comment.getPost().getPostId())
			.content(comment.getContent())
			.userDto(UserDto.from(comment.getUser()))
			.createdDate(comment.getCreatedDate())
			.modifiedDate(comment.getModifiedDate())
			.build();
	}

}
