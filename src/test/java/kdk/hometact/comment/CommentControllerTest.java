package kdk.hometact.comment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(controllers = CommentController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
@AutoConfigureDataJpa
class CommentControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private CommentService commentService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 댓글_등록_성공() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.uploadComment(any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment";
		String requestBody = convertCommentDtoJson(1L, commentContent);
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		actions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("content").value(commentContent));
	}

	@Test
	void 댓글_등록_예외_게시글아이디가_없음() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.uploadComment(any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment";
		String requestBody = "{"
			+ " \"content\" : \"content\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 댓글_등록_예외_내용이_없음() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.uploadComment(any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment";
		String requestBody = "{"
			+ " \"postId\" : \"1\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 댓글_등록_예외_내용_길이_초과() throws Exception {
		// given
		String commentContent = "commentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcomm";
		given(commentService.uploadComment(any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment";
		String requestBody = convertCommentDtoJson(1L, commentContent);
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 댓글_등록_예외_JSON_포맷_에러() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.uploadComment(any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment";
		String requestBody = "{"
			+ " \"postId\" : \"1\", "
			+ " \"content\" : \"content\" ";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectNotReadableException(actions);
	}

	@Test
	void 댓글_수정_성공() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.updateComment(any(), any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment/1";
		String requestBody = convertUpdateCommentDtoJson(commentContent);
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("content").value(commentContent));
	}

	@Test
	void 댓글_수정_예외_내용이_없음() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.updateComment(any(), any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment/1";
		String requestBody = "{"
			+ "}";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}


	@Test
	void 댓글_수정_예외_내용_길이_초과() throws Exception {
		// given
		String commentContent = "commentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcommentcomm";
		given(commentService.updateComment(any(), any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment/1";
		String requestBody = convertCommentDtoJson(1L, commentContent);
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 댓글_수정_예외_JSON_포맷_에러() throws Exception {
		// given
		String commentContent = "comment";
		given(commentService.updateComment(any(), any()))
			.willReturn(createCommentDto(commentContent));

		// when
		String requestUrl = "/api/comment/1";
		String requestBody = "{"
			+ " \"content\" : \"content\" ";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectNotReadableException(actions);
	}

	@Test
	void 댓글_삭제_성공() throws Exception {
		// given
		doNothing().when(commentService).deleteComment(any());

		// when
		String requestUrl = "/api/comment/1";
		ResultActions actions = deleteRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk());
	}

	@Test
	void 댓글_삭제_예외_댓글이_없음() {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
			.when(commentService).deleteComment(any());

		// when
		String requestUrl = "/api/comment/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> deleteRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	private CommentDto createCommentDto(String commentContent) {
		return CommentDto.builder()
			.content(commentContent)
			.build();
	}

	private String convertCommentDtoJson(Long postId, String commentContent) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"postId\" : \"")
			.append(postId)
			.append("\",")
			.append(" \"content\" : \"")
			.append(commentContent)
			.append("\"")
			.append("}"));
	}

	private String convertUpdateCommentDtoJson(String commentContent) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"content\" : \"")
			.append(commentContent)
			.append("\"")
			.append("}"));
	}

	private ResultActions postRequest(String requestUrl, String content)
		throws Exception {
		return mvc.perform(
			post(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(content)
		);
	}

	private ResultActions patchRequest(String requestUrl, String content)
		throws Exception {
		return mvc.perform(
			patch(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(content)
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

	private void expectValidException(ResultActions actions) throws Exception {
		actions
			.andExpect(
				(result) -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(
					MethodArgumentNotValidException.class))
			);
	}

	private void expectNotReadableException(ResultActions actions) throws Exception {
		actions
			.andExpect(
				(result) -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(
					HttpMessageNotReadableException.class))
			);
	}

}