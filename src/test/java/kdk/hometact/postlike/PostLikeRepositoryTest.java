package kdk.hometact.postlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import kdk.hometact.user.auth.Authority;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
class PostLikeRepositoryTest {

	@Autowired
	private PostLikeRepository postLikeRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Test
	void 좋아요_등록() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));
		Post post = postRepository.save(createPost("title", "content", email));

		// when
		PostLike result = postLikeRepository.save(createPostLike(user, post));

		// then
		assertThat(user).isEqualTo(result.getUser());
		assertThat(post).isEqualTo(result.getPost());
	}

	@Test
	void 좋아요_취소() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));
		Post post = postRepository.save(createPost("title", "content", email));
		PostLike postLike = postLikeRepository.save(createPostLike(user, post));

		// when
		postLikeRepository.delete(postLike);
		EntityNotFoundException e = assertThrows(
			EntityNotFoundException.class,
			() -> postLikeRepository.findById(postLike.getPostLikeId()).orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	private User createUser(String email, String password, String nickname) {
		User user = User.builder()
			.email(email)
			.password(passwordEncoder.encode(password))
			.nickname(nickname)
			.authorities(Collections.singleton(Authority.createUserRole()))
			.build();
		return user;
	}

	private Post createPost(String title, String content, String email) {
		return Post.builder()
			.user(userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			)
			.title(title)
			.content(content)
			.build();
	}

	private PostLike createPostLike(User user, Post post) {
		return PostLike.builder()
			.post(post)
			.user(user)
			.build();
	}
}