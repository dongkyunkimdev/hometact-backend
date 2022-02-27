package kdk.hometact.post;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.security.SecurityConfig;
import kdk.hometact.user.dto.UserDto;
import org.assertj.core.api.AbstractThrowableAssert;
import org.hamcrest.Matchers;
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

@WebMvcTest(controllers = PostController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
@AutoConfigureDataJpa
class PostControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private PostService postService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new PostController(postService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 게시글_등록_성공() throws Exception {
		// given
		String title = "title";
		String content = "content";
		Long postCategoryId = 1L;
		given(postService.uploadPost(any())).willReturn(
			createPostDto(title, content)
		);

		// when
		String requestUrl = "/api/post";
		String requestBody = convertUploadPostDtoJson(title, content, postCategoryId);
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		actions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("title").value(title))
			.andExpect(jsonPath("content").value(content));
	}

	@Test
	void 게시글_등록_예외_제목이_없음() throws Exception {
		// when
		String requestUrl = "/api/post";
		String requestBody = "{"
			+ " \"content\" : \"content\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_등록_예외_제목_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/post";
		String requestBody = "{"
			+ " \"title\" : \"titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitlet\", "
			+ " \"content\" : \"content\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_등록_예외_내용이_없음() throws Exception {
		// when
		String requestUrl = "/api/post";
		String requestBody = "{"
			+ " \"title\" : \"title\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_등록_예외_내용_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/post";
		String requestBody = "{"
			+ " \"title\" : \"title\", "
			+ " \"content\" : \"contentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentconte\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_등록_예외_JSON_포맷_에러() throws Exception {
		// when
		String requestUrl = "/api/post";
		String requestBody = "{"
			+ " \"title\" : \"title\", "
			+ " \"content\" : \"content\" ";
		ResultActions actions = postRequest(requestUrl, requestBody);

		// then
		expectNotReadableException(actions);
	}

	@Test
	void 전체게시글_조회_성공() throws Exception {
		// given
		String title = "title";
		String content = "content";
		int listSize = 3;
		given(postService.selectAllPost(any())).willReturn(
			createPostDtoList(title, content, listSize)
		);

		// when
		String requestUrl = "/api/post/get";
		ResultActions actions = getRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(listSize)))
			.andExpect(jsonPath("$[0].title").value(title))
			.andExpect(jsonPath("$[0].content").value(content));
	}

	@Test
	void 게시글_조회_성공() throws Exception {
		// given
		String title = "title";
		String content = "content";
		given(postService.selectPost(any())).willReturn(
			createPostDto(title, content)
		);

		// when
		String requestUrl = "/api/post/get/1";
		ResultActions actions = getRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("title").value(title))
			.andExpect(jsonPath("content").value(content));
	}

	@Test
	void 게시글_조회_예외_게시글이_없음() {
		// given
		given(postService.selectPost(any())).willThrow(
			new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		// when
		String requestUrl = "/api/post/get/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> getRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void 게시글_삭제_성공() throws Exception {
		// given
		doNothing().when(postService).deletePost(any());

		// when
		String requestUrl = "/api/post/1";
		ResultActions actions = deleteRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk());
	}

	@Test
	void 게시글_삭제_예외_게시글이_없음() {
		// given
		doThrow(new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
			.when(postService).deletePost(any());

		// when
		String requestUrl = "/api/post/1";
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> deleteRequest(requestUrl)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void 게시글_수정_성공() throws Exception {
		// given
		String updateTitle = "updateTitle";
		String updateContent = "updateContent";
		Long postCategoryId = 1L;
		given(postService.updatePost(any(), any())).willReturn(
			createPostDto(updateTitle, updateContent)
		);

		// when
		String requestUrl = "/api/post/1";
		String requestBody = convertUploadPostDtoJson(updateTitle, updateContent, postCategoryId);
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("title").value(updateTitle))
			.andExpect(jsonPath("content").value(updateContent));
	}

	@Test
	void 게시글_수정_예외_게시글이_없음() throws Exception {
		// given
		String updateTitle = "updateTitle";
		String updateContent = "updateContent";
		Long postCategoryId = 1L;
		given(postService.updatePost(any(), any())).willThrow(
			new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		// when
		String requestUrl = "/api/post/1";
		String requestBody = convertUploadPostDtoJson(updateTitle, updateContent, postCategoryId);
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> patchRequest(requestUrl, requestBody)
		);

		// then
		o.getCause().isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void 게시글_수정_예외_제목이_없음() throws Exception {
		// when
		String requestUrl = "/api/post/1";
		String requestBody = "{"
			+ " \"content\" : \"content\" "
			+ "}";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_수정_예외_제목_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/post/1";
		String requestBody = "{"
			+ " \"title\" : \"titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitlet\", "
			+ " \"content\" : \"content\" "
			+ "}";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_수정_예외_내용이_없음() throws Exception {
		// when
		String requestUrl = "/api/post/1";
		String requestBody = "{"
			+ " \"title\" : \"title\" "
			+ "}";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_수정_예외_내용_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/post/1";
		String requestBody = "{"
			+ " \"title\" : \"title\", "
			+ " \"content\" : \"contentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentconte\" "
			+ "}";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectValidException(actions);
	}

	@Test
	void 게시글_수정_예외_JSON_포맷_에러() throws Exception {
		// when
		String requestUrl = "/api/post/1";
		String requestBody = "{"
			+ " \"title\" : \"title\", "
			+ " \"content\" : \"content\" ";
		ResultActions actions = patchRequest(requestUrl, requestBody);

		// then
		expectNotReadableException(actions);
	}

	private PostDto createPostDto(String title, String content) {
		return PostDto.builder()
			.title(title)
			.content(content)
			.build();
	}

	private List<PostDto> createPostDtoList(String title, String content, int length) {
		List<PostDto> list = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			list.add(PostDto.builder()
				.postId(Long.valueOf(i))
				.userDto(UserDto.builder().build())
				.title(title)
				.content(content)
				.build());
		}
		return list;
	}

	private String convertPostDtoJson(String title, String content) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"title\" : \"")
			.append(title)
			.append("\",")
			.append(" \"content\" : \"")
			.append(content)
			.append("\"")
			.append("}"));
	}

	private String convertUploadPostDtoJson(String title, String content, Long postCategoryId) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"title\" : \"")
			.append(title)
			.append("\",")
			.append(" \"content\" : \"")
			.append(content)
			.append("\",")
			.append(" \"postCategoryId\" : \"")
			.append(postCategoryId)
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

	private ResultActions getRequest(String requestUrl) throws Exception {
		return mvc.perform(
			get(requestUrl)
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