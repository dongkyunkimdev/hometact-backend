package kdk.hometact.postlike;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.postlike.exception.PostLikeAlreadyAddException;
import kdk.hometact.security.SecurityConfig;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(controllers = PostLikeController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
@AutoConfigureDataJpa
class PostLikeControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private PostLikeService postLikeService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new PostLikeController(postLikeService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 좋아요_등록_성공() throws Exception {
		// when
		String requestUrl = "/postlike/1";
		ResultActions actions = postRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk());
	}

	@Test
	void 좋아요_등록_예외_게시글이_없음() throws Exception {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
			.when(postLikeService).addLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> postRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void 좋아요_등록_예외_사용자가_없음() throws Exception {
		// given
		doThrow(new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			.when(postLikeService).addLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> postRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(UsernameNotFoundException.class);
	}

	@Test
	void 좋아요_등록_예외_이미_등록됨() throws Exception {
		// given
		doThrow(new PostLikeAlreadyAddException()).when(postLikeService).addLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> postRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(PostLikeAlreadyAddException.class);
	}

	@Test
	void 좋아요_취소_성공() throws Exception {
		// when
		String requestUrl = "/postlike/1";
		ResultActions actions = deleteRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk());
	}

	@Test
	void 좋아요_취소_예외_게시글이_없음() throws Exception {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
			.when(postLikeService).cancelLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> deleteRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void 좋아요_취소_예외_사용자가_없음() throws Exception {
		// given
		doThrow(new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
			.when(postLikeService).cancelLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> deleteRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(UsernameNotFoundException.class);
	}

	@Test
	void 좋아요_취소_예외_좋아요가_없음() throws Exception {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
			.when(postLikeService).cancelLike(any());

		// when
		String requestUrl = "/postlike/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> deleteRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	private ResultActions postRequest(String requestUrl) throws Exception {
		return mvc.perform(
			post(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
		);
	}

	private ResultActions deleteRequest(String requestUrl) throws Exception {
		return mvc.perform(
			delete(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
		);
	}
}