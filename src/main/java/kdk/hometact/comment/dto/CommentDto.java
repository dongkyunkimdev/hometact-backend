package kdk.hometact.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import kdk.hometact.comment.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDto {

	@NotNull
	private Long postId;

	@NotNull
	@Size(max = 500)
	private String content;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime createdDate;

	@JsonProperty(access = Access.READ_ONLY)
	private LocalDateTime modifiedDate;

	@Builder
	public CommentDto(@NotNull Long postId,
		@NotNull @Size(max = 500) String content, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.postId = postId;
		this.content = content;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static CommentDto from(Comment comment) {
		return CommentDto.builder()
			.postId(comment.getPost().getPostId())
			.content(comment.getContent())
			.createdDate(comment.getCreatedDate())
			.modifiedDate(comment.getModifiedDate())
			.build();
	}

}
