package kdk.hometact.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import kdk.hometact.user.auth.Authority;
import kdk.hometact.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;
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
	void 댓글_등록() {
		// given
		String postWriter = "test1@test.com";
		String commentWriter = "test2@test.com";
		userRepository.save(createUser(postWriter, "password", "test1"));
		userRepository.save(createUser(commentWriter, "password", "test2"));
		Long postId = postRepository.save(createPost("title", "content", postWriter)).getPostId();

		// when
		String commentContent = "comment";
		Comment result = commentRepository
			.save(createComment(commentContent, postId, commentWriter));

		// then
		assertThat(commentContent).isEqualTo(result.getContent());
		assertThat(commentWriter).isEqualTo(result.getUser().getEmail());
		assertThat(postId).isEqualTo(result.getPost().getPostId());
	}

	@Test
	void 댓글_조회() {
		// given
		String postWriter = "test1@test.com";
		String commentWriter = "test2@test.com";
		String commentContent = "comment";
		userRepository.save(createUser(postWriter, "password", "test1"));
		userRepository.save(createUser(commentWriter, "password", "test2"));
		Long postId = postRepository.save(createPost("title", "content", postWriter)).getPostId();
		Comment comment = commentRepository
			.save(createComment(commentContent, postId, commentWriter));

		// when
		Comment result = commentRepository.findById(comment.getCommentId()).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		// then
		assertThat(commentContent).isEqualTo(result.getContent());
		assertThat(commentWriter).isEqualTo(result.getUser().getEmail());
		assertThat(postId).isEqualTo(result.getPost().getPostId());
	}

	@Test
	void 게시글_전체댓글_조회() {
		// given
		String postWriter = "test1@test.com";
		String commentWriter = "test2@test.com";
		String commentWriter2 = "test3@test.com";
		String commentContent = "comment";
		String commentContent2 = "comment2";
		userRepository.save(createUser(postWriter, "password", "test1"));
		userRepository.save(createUser(commentWriter, "password", "test2"));
		userRepository.save(createUser(commentWriter2, "password", "test3"));
		Post post = postRepository.save(createPost("title", "content", postWriter));
		commentRepository.save(createComment(commentContent, post.getPostId(), commentWriter));
		commentRepository.save(createComment(commentContent2, post.getPostId(), commentWriter2));

		// when
		List<Comment> resultList = commentRepository.findAllByPost(post);

		// then
		assertThat(2).isEqualTo(resultList.size());
		assertThat(1).isEqualTo(resultList.stream().filter(
			comment -> comment.getUser().getEmail().equals(commentWriter)
		).count());
		assertThat(1).isEqualTo(resultList.stream().filter(
			comment -> comment.getUser().getEmail().equals(commentWriter2)
		).count());
		assertThat(1).isEqualTo(resultList.stream().filter(
			comment -> comment.getContent().equals(commentContent)
		).count());
		assertThat(1).isEqualTo(resultList.stream().filter(
			comment -> comment.getContent().equals(commentContent2)
		).count());
		assertThat(post).isEqualTo(resultList.get(0).getPost());
	}

	@Test
	void 댓글_수정() {
		// given
		String postWriter = "test1@test.com";
		String commentWriter = "test2@test.com";
		String commentContent = "comment";
		userRepository.save(createUser(postWriter, "password", "test1"));
		userRepository.save(createUser(commentWriter, "password", "test2"));
		Long postId = postRepository.save(createPost("title", "content", postWriter)).getPostId();
		Comment comment = commentRepository
			.save(createComment(commentContent, postId, commentWriter));

		// when
		String updateComment = "updateComment";
		CommentDto commentDto = createCommentDto(updateComment);
		comment.update(commentDto);

		// then
		assertThat(updateComment).isEqualTo(comment.getContent());
		assertThat(commentWriter).isEqualTo(comment.getUser().getEmail());
		assertThat(postId).isEqualTo(comment.getPost().getPostId());
	}

	@Test
	void 댓글_삭제() {
		// given
		String postWriter = "test1@test.com";
		String commentWriter = "test2@test.com";
		userRepository.save(createUser(postWriter, "password", "test1"));
		userRepository.save(createUser(commentWriter, "password", "test2"));
		Long postId = postRepository.save(createPost("title", "content", postWriter)).getPostId();
		String commentContent = "comment";
		Comment comment = commentRepository
			.save(createComment(commentContent, postId, commentWriter));

		// when
		commentRepository.delete(comment);
		EntityNotFoundException e = assertThrows(
			EntityNotFoundException.class,
			() -> commentRepository.findById(comment.getCommentId()).orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());

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

	private Post createPost(String title, String content, String email) {
		return Post.builder()
			.user(userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			)
			.title(title)
			.content(content)
			.build();
	}

	private Comment createComment(String commentContent, Long postId, String email) {
		return Comment.builder()
			.post(postRepository.findById(postId).orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			))
			.user(userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			)
			.content(commentContent)
			.build();
	}

	private CommentDto createCommentDto(String updateComment) {
		return CommentDto.builder()
			.content(updateComment)
			.build();
	}

}