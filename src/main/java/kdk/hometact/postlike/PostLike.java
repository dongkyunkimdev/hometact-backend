package kdk.hometact.postlike;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kdk.hometact.post.Post;
import kdk.hometact.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

	@Id
	@Column(name = "post_like_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postLikeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public PostLike(Long postLikeId, Post post, User user) {
		this.postLikeId = postLikeId;
		this.post = post;
		this.user = user;
	}
}
