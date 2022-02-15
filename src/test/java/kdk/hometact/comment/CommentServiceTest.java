package kdk.hometact.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CommentServiceTest {

	@InjectMocks
	private CommentService commentService;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		commentService = new CommentService(commentRepository, postRepository, userRepository);
	}

	@Test
	void 댓글_업로드_성공() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		String commentContent = "comment";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email)).willReturn(
			Optional.ofNullable(user)
		);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(commentRepository.save(any())).willReturn(
			Comment.builder()
				.post(post)
				.user(user)
				.content(commentContent)
				.build()
		);

		// when
		CommentDto result = commentService.uploadComment(createCommentDto(commentContent));

		// then
		assertThat(result.getContent()).isEqualTo(commentContent);

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 댓글_업로드_예외_게시글이_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		String commentContent = "comment";
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> commentService.uploadComment(createCommentDto(commentContent))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 댓글_업로드_예외_사용자가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		String commentContent = "comment";
		Post post = mock(Post.class);
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> commentService.uploadComment(createCommentDto(commentContent))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 댓글_조회_성공() {
		// given
		String commentContent = "comment";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		// when
		CommentDto result = commentService.selectComment(1L);

		// then
		assertThat(result.getContent()).isEqualTo(commentContent);
	}

	@Test
	void 댓글_조회_예외_댓글이_없음() {
		// given
		given(commentRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> commentService.selectComment(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 게시글_전체댓글_조회_성공() {
		// given
		String commentContent = "comment";
		User user = mock(User.class);
		Post post = mock(Post.class);
		Comment comment = createComment(commentContent, post, user);
		List<Comment> commentList = List.of(comment, comment, comment);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(commentRepository.findAllByPost(any())).willReturn(commentList);

		// when
		List<CommentDto> resultList = commentService.selectAllPostComment(1L);

		// then
		assertThat(3).isEqualTo(resultList.size());
		assertThat(commentContent).isEqualTo(resultList.get(0).getContent());
	}

	@Test
	void 게시글_전체댓글_조회_예외_게시글이_없음() {
		// given
		String commentContent = "comment";
		User user = mock(User.class);
		Post post = mock(Post.class);
		Comment comment = createComment(commentContent, post, user);
		List<Comment> commentList = List.of(comment, comment, comment);

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> commentService.selectAllPostComment(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 댓글_수정_성공_사용자_일치() {
		// given
		String commentContent = "comment";
		String updateCommentContent = "updateComment";
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn("test1@test.com");
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(email);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		CommentDto result = commentService
			.updateComment(1L, createCommentDto(updateCommentContent));

		// then
		assertThat(result.getContent()).isEqualTo(updateCommentContent);
	}

	@Test
	void 댓글_수정_성공_관리자() {
		// given
		String commentContent = "comment";
		String updateCommentContent = "updateComment";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn(email);
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(unmatchedEmail);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		CommentDto result = commentService
			.updateComment(1L, createCommentDto(updateCommentContent));

		// then
		assertThat(result.getContent()).isEqualTo(updateCommentContent);
	}

	@Test
	void 댓글_수정_예외_댓글이_없음() {
		// given
		String commentContent = "comment";
		given(commentRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> commentService.updateComment(1L, createCommentDto(commentContent))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 댓글_수정_예외_아이디_불일치_권한이_없음() {
		// given
		String commentContent = "comment";
		String updateCommentContent = "updateComment";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn(email);
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(unmatchedEmail);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		AccessDeniedException e = assertThrows(AccessDeniedException.class,
			() -> commentService.updateComment(1L, createCommentDto(updateCommentContent))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}


	@Test
	void 댓글_삭제_성공_사용자_일치() {
		// given
		String commentContent = "comment";
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn("test1@test.com");
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(email);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		commentService.deleteComment(1L);
	}

	@Test
	void 댓글_삭제_성공_관리자() {
		// given
		String commentContent = "comment";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn(email);
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(unmatchedEmail);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		commentService.deleteComment(1L);
	}

	@Test
	void 댓글_삭제_예외_댓글이_없음() {
		// given
		given(commentRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> commentService.deleteComment(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 댓글_삭제_예외_아이디_불일치_권한이_없음() {
		// given
		String commentContent = "comment";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(user.getEmail()).willReturn(email);
		given(commentRepository.findById(any())).willReturn(
			Optional.ofNullable(createComment(commentContent, post, user))
		);

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		UserDetails userDetails = mock(UserDetails.class);
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn(unmatchedEmail);
		doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))).when(userDetails)
			.getAuthorities();
		SecurityContextHolder.setContext(securityContext);

		// when
		AccessDeniedException e = assertThrows(AccessDeniedException.class,
			() -> commentService.deleteComment(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

	private Comment createComment(String commentContent, Post post, User user) {
		return Comment.builder()
			.post(post)
			.user(user)
			.content(commentContent)
			.build();
	}

	private CommentDto createCommentDto(String updateComment) {
		return CommentDto.builder()
			.content(updateComment)
			.build();
	}

}