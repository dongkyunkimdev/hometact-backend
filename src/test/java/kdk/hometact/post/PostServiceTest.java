package kdk.hometact.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class PostServiceTest {

	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		postService = new PostService(postRepository, userRepository);
	}

	@Test
	void 게시글_업로드_성공() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		User user = mock(User.class);
		String email = "test1@test.com";
		String title = "title";
		String content = "content";
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email)).willReturn(
			Optional.ofNullable(user)
		);
		given(postRepository.save(any())).willReturn(createPost(title, content, user));

		// when
		PostDto result = postService.uploadPost(createPostDto(title, content));

		// then
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getContent()).isEqualTo(content);

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 게시글_업로드_예외_사용자가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		String title = "title";
		String content = "content";
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(null));

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> postService.uploadPost(createPostDto(title, content))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 전체게시글_조회_성공() {
		// given
		String title = "title";
		String content = "content";
		int listSize = 3;
		User user = mock(User.class);
		given(postRepository.findAll((Pageable) any())).willReturn(
			new PageImpl<>(createPostList(title, content, user, listSize))
		);

		// when
		List<PostDto> resultList = postService.selectAllPost(PageRequest.of(0, 10));

		// then
		assertThat(resultList.size()).isEqualTo(listSize);
		assertThat(resultList.stream()
			.filter(postDto -> postDto.getTitle().equals(title))
			.count())
			.isEqualTo(listSize);
		assertThat(resultList.stream()
			.filter(postDto -> postDto.getContent().equals(content))
			.count())
			.isEqualTo(listSize);
	}

	@Test
	void 게시글_조회_성공() {
		// given
		String title = "title";
		String content = "content";
		User user = mock(User.class);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
		);

		// when
		PostDto result = postService.selectPost(1L);

		// then
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getContent()).isEqualTo(content);
	}

	@Test
	void 게시글_조회_예외_게시글이_없음() {
		// given
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postService.selectPost(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 게시글_삭제_성공_사용자_일치() {
		// given
		String title = "title";
		String content = "content";
		String email = "test1@test.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
		);
		doNothing().when(postRepository).delete(any());

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
		postService.deletePost(1L);
	}

	@Test
	void 게시글_삭제_성공_관리자() {
		// given
		String title = "title";
		String content = "content";
		String email = "test1@test.com";
		String unmatchedEmail = "admin@admin.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
		);
		doNothing().when(postRepository).delete(any());

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
		postService.deletePost(1L);
	}

	@Test
	void 게시글_삭제_예외_게시글이_없음() {
		// given
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postService.deletePost(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 게시글_삭제_예외_아이디_불일치_권한이_없음() {
		// given
		String title = "title";
		String content = "content";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
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
			() -> postService.deletePost(1L)
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

	@Test
	void 게시글_수정_성공_사용자_일치() {
		// given
		String title = "title";
		String content = "content";
		String updateTitle = "updateTitle";
		String updateContent = "updateContent";
		String email = "test1@test.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
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
		PostDto result = postService
			.updatePost(1L, createPostDto(updateTitle, updateContent));

		// then
		assertThat(result.getTitle()).isEqualTo(updateTitle);
		assertThat(result.getContent()).isEqualTo(updateContent);
	}

	@Test
	void 게시글_수정_성공_관리자() {
		// given
		String title = "title";
		String content = "content";
		String updateTitle = "updateTitle";
		String updateContent = "updateContent";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
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
		PostDto result = postService
			.updatePost(1L, createPostDto(updateTitle, updateContent));

		// then
		assertThat(result.getTitle()).isEqualTo(updateTitle);
		assertThat(result.getContent()).isEqualTo(updateContent);
	}

	@Test
	void 게시글_수정_예외_게시글이_없음() {
		// given
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postService.updatePost(1L, mock(PostDto.class))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 게시글_수정_예외_아이디_불일치_권한이_없음() {
		// given
		String title = "title";
		String content = "content";
		String email = "test1@test.com";
		String unmatchedEmail = "test2@test.com";
		User user = mock(User.class);
		given(user.getEmail()).willReturn(email);
		given(postRepository.findById(any())).willReturn(
			Optional.ofNullable(createPost(title, content, user))
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
			() -> postService.updatePost(1L, mock(PostDto.class))
		);

		// then
		assertThat(e.getMessage()).isEqualTo(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

	private PostDto createPostDto(String title, String content) {
		return PostDto.builder()
			.title(title)
			.content(content)
			.build();
	}

	private Post createPost(String title, String content, User user) {
		return Post.builder()
			.postId(1L)
			.user(user)
			.title(title)
			.content(content)
			.build();
	}

	private List<Post> createPostList(String title, String content, User user, int length) {
		List<Post> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(Post.builder()
				.postId(Long.valueOf(i))
				.user(user)
				.title(title)
				.content(content)
				.build());
		}
		return list;
	}

}