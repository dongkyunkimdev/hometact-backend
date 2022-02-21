package kdk.hometact.post;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import kdk.hometact.BaseTimeEntity;
import kdk.hometact.comment.Comment;
import kdk.hometact.post.dto.UploadPostDto;
import kdk.hometact.postcategory.PostCategory;
import kdk.hometact.postlike.PostLike;
import kdk.hometact.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

	@Id
	@Column(name = "post_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "title", nullable = false)
	@Size(max = 500)
	private String title;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	@Size(max = 5000)
	private String content;

	@Column(name = "view")
	private Long view;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	List<PostLike> postLikes = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_category_id")
	private PostCategory postCategory;

	@Builder
	public Post(Long postId, User user,
		@Size(max = 500) String title,
		@Size(max = 5000) String content, PostCategory postCategory) {
		this.postId = postId;
		this.user = user;
		this.title = title;
		this.content = content;
		this.postCategory = postCategory;
	}

	public void update(UploadPostDto postDto, PostCategory postCategory) {
		this.title = postDto.getTitle();
		this.content = postDto.getContent();
		this.postCategory = postCategory;
	}

	public void addViewCnt() {
		this.view++;
	}

	@PrePersist
	private void initView() {
		this.view = this.view == null ? 0 : this.view;
	}
}
