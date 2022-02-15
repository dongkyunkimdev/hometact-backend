package kdk.hometact.postlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import kdk.hometact.user.auth.Authority;
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

	@Test
	void 유저와_게시글로_좋아요_조회() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));
		Post post = postRepository.save(createPost("title", "content", email));
		PostLike postLike = postLikeRepository.save(createPostLike(user, post));

		// when
		PostLike result = postLikeRepository.findByPostAndUser(post, user).orElse(null);

		// then
		assertThat(user).isEqualTo(result.getUser());
		assertThat(post).isEqualTo(result.getPost());
	}

	@Test
	void 유저와_게시글로_좋아요_조회_존재_TRUE() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));
		Post post = postRepository.save(createPost("title", "content", email));
		PostLike postLike = postLikeRepository.save(createPostLike(user, post));

		// when
		boolean result = postLikeRepository.existsByPostAndUser(post, user);

		// then
		assertTrue(result);
	}

	@Test
	void 유저와_게시글로_좋아요_조회_존재_FALSE() {
		// given
		String email = "test1@test.com";
		User user = userRepository.save(createUser(email, "password", "test1"));
		Post post = postRepository.save(createPost("title", "content", email));

		// when
		boolean result = postLikeRepository.existsByPostAndUser(post, user);

		// then
		assertFalse(result);
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