package kdk.hometact.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import kdk.hometact.BaseTimeEntity;
import kdk.hometact.comment.Comment;
import kdk.hometact.post.Post;
import kdk.hometact.postlike.PostLike;
import kdk.hometact.user.auth.Authority;
import kdk.hometact.user.dto.UpdateNicknameDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "email", nullable = false, unique = true)
	@Size(min = 3, max = 100)
	private String email;

	@Column(name = "password", nullable = false)
	@Size(min = 8, max = 100)
	private String password;

	@Column(name = "nickname", nullable = false, unique = true)
	@Size(min = 3, max = 20)
	private String nickname;

	@OneToMany(mappedBy = "user")
	List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	List<PostLike> postLikes = new ArrayList<>();

	@ManyToMany
	@JoinTable(
		name = "user_authority",
		joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
		inverseJoinColumns = {
			@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
	private Set<Authority> authorities;

	@Builder
	public User(Long userId, String email, String password, String nickname,
		Set<Authority> authorities) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.authorities = authorities;
	}

	public void updateNickname(UpdateNicknameDto updateNicknameDto) {
		this.nickname = updateNicknameDto.getNickname();
	}
}