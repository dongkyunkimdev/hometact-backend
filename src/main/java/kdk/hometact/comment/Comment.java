package kdk.hometact.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import kdk.hometact.BaseTimeEntity;
import kdk.hometact.comment.dto.UpdateCommentDto;
import kdk.hometact.post.Post;
import kdk.hometact.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

	@Id
	@Column(name = "comment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "content", nullable = false)
	@Size(max = 500)
	private String content;

	@Builder
	public Comment(Long commentId, Post post, User user,
		@Size(max = 500) String content) {
		this.commentId = commentId;
		this.post = post;
		this.user = user;
		this.content = content;
	}

	public void update(UpdateCommentDto updateCommentDto) {
		this.content = updateCommentDto.getContent();
	}

}
