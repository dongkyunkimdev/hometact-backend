package kdk.hometact.post;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import kdk.hometact.user.auth.Authority;
import kdk.hometact.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;
	@MockBean
	private UserRepository userRepository;
	@Autowired
	private UserRepository realUserRepository;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@Test
	void 게시글_등록() {
		// given
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(User.builder().build())
		);
		Post post = createPost("title", "content");

		// when
		Post result = postRepository.save(post);

		// then
		expectEquals(post, result);
	}

	@Test
	void 전체게시글_조회() {
		// given
		String title = "title";
		String content = "content";
		User user1 = realUserRepository.save(createUser("test1@test.com", "password", "test1"));
		User user2 = realUserRepository.save(createUser("test2@test.com", "password", "test2"));
		postRepository.save(createPostWithRealUser(title, content, user1));
		postRepository.save(createPostWithRealUser(title, content, user1));
		postRepository.save(createPostWithRealUser(title, content, user2));
		PageRequest pageRequest = PageRequest.of(0, 10);

		// when
		List<Post> resultList = postRepository.findAll(pageRequest).getContent();

		// then
		assertThat(resultList.size()).isEqualTo(3);
		assertThat(resultList.get(0).getTitle()).isEqualTo(title);
		assertThat(resultList.get(0).getContent()).isEqualTo(content);
		assertThat(resultList.get(0).getUser()).isEqualTo(user1);
	}

	@Test
	void 게시글_조회() {
		// given
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(User.builder().build())
		);
		Post post = postRepository.save(createPost("title", "content"));

		// when
		Post result = postRepository.findById(post.getPostId()).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		// then
		expectEquals(post, result);
	}

	@Test
	void 게시글_수정() {
		// given
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(User.builder().build())
		);
		Post post = postRepository.save(createPost("title", "content"));

		// when
		String updateTitle = "updateTitle";
		String updateContent = "updateContent";
		PostDto postDto = createPostDto(updateTitle, updateContent);
		post.update(postDto);
		Post result = postRepository.findById(post.getPostId()).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		// then
		assertThat(result.getTitle()).isEqualTo(updateTitle);
		assertThat(result.getContent()).isEqualTo(updateContent);
	}

	@Test
	void 게시글_삭제() {
		// given
		given(userRepository.findOneWithAuthoritiesByEmail(any())).willReturn(
			ofNullable(User.builder().build())
		);
		Post post = postRepository.save(createPost("title", "content"));

		// when
		postRepository.delete(post);
		EntityNotFoundException e = assertThrows(
			EntityNotFoundException.class,
			() -> postRepository.findById(post.getPostId()).orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	private Post createPost(String title, String content) {
		return Post.builder()
			.user(userRepository.findOneWithAuthoritiesByEmail("test1@test.com").orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			)
			.title(title)
			.content(content)
			.build();
	}

	private Post createPostWithRealUser(String title, String content, User user) {
		return Post.builder()
			.user(user)
			.title(title)
			.content(content)
			.build();
	}

	private PostDto createPostDto(String title, String content) {
		return PostDto.builder()
			.postId(1L)
			.userId(1L)
			.title(title)
			.content(content)
			.createdDate(LocalDateTime.now())
			.modifiedDate(LocalDateTime.now())
			.build();
	}

	private User createUser(String email, String password, String nickname) {
		UserDto userDto = UserDto.builder()
			.email(email)
			.password(password)
			.nickname(nickname).build();

		User user = User.builder()
			.email(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword()))
			.nickname(userDto.getNickname())
			.authorities(Collections.singleton(Authority.createUserRole()))
			.build();
		return user;
	}

	private void expectEquals(Post post, Post result) {
		assertThat(post).isEqualTo(result);
		assertThat(post.getPostId()).isEqualTo(result.getPostId());
		assertThat(post.getTitle()).isEqualTo(result.getTitle());
		assertThat(post.getContent()).isEqualTo(result.getContent());
		assertThat(post.getUser()).isEqualTo(result.getUser());
		assertThat(post.getCreatedDate()).isEqualTo(result.getCreatedDate());
		assertThat(post.getModifiedDate()).isEqualTo(result.getModifiedDate());
		assertThat(0L).isEqualTo(result.getView());
	}
}