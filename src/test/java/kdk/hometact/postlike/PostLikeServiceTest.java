package kdk.hometact.postlike;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Optional;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.postlike.exception.PostLikeAlreadyAddException;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class PostLikeServiceTest {

	@InjectMocks
	private PostLikeService postLikeService;
	@Mock
	private PostLikeRepository postLikeRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		postLikeService = new PostLikeService(postLikeRepository, postRepository, userRepository);
	}

	@Test
	void 좋아요_등록_성공() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(user));

		// when
		postLikeService.addLike(1L);

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 좋아요_등록_예외_게시글이_없음() {
		// given
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postLikeService.addLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 좋아요_등록_예외_사용자가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(null));

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> postLikeService.addLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 좋아요_등록_예외_이미_등록됨() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(user));
		given(postLikeRepository.existsByPostAndUser(any(), any())).willReturn(true);

		// when
		PostLikeAlreadyAddException e = assertThrows(PostLikeAlreadyAddException.class,
			() -> postLikeService.addLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage())
			.isEqualTo(ErrorCode.POSTLIKE_DUPLICATION.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 좋아요_취소_성공() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		PostLike postLike = mock(PostLike.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(user));
		given(postLikeRepository.findByPostAndUser(any(), any())).willReturn(
			Optional.ofNullable(postLike));

		// when
		postLikeService.cancelLike(1L);

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 좋아요_취소_예외_게시글이_없음() {
		// given
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postLikeService.cancelLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());
	}

	@Test
	void 좋아요_취소_예외_사용자가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(null));

		// when
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
			() -> postLikeService.cancelLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}

	@Test
	void 좋아요_취소_예외_좋아요가_없음() {
		// given
		MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class);
		String email = "test1@test.com";
		User user = mock(User.class);
		Post post = mock(Post.class);
		PostLike postLike = mock(PostLike.class);
		given(postRepository.findById(any())).willReturn(Optional.ofNullable(post));
		given(SecurityUtil.getCurrentUsername()).willReturn(Optional.ofNullable(email));
		given(userRepository.findOneWithAuthoritiesByEmail(email))
			.willReturn(Optional.ofNullable(user));
		given(postLikeRepository.findByPostAndUser(any(), any())).willReturn(
			Optional.ofNullable(null));

		// when
		EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
			() -> postLikeService.cancelLike(1L)
		);

		// then
		Assertions.assertThat(e.getMessage()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getMessage());

		// end
		mockSecurityUtil.close();
	}
}