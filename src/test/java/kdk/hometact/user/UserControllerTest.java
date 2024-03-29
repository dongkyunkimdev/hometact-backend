package kdk.hometact.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.security.SecurityConfig;
import kdk.hometact.security.jwt.EnumToken;
import kdk.hometact.security.jwt.JwtFilter;
import kdk.hometact.security.jwt.JwtService;
import kdk.hometact.user.auth.dto.AuthorityDto;
import kdk.hometact.user.dto.UserDto;
import kdk.hometact.user.exception.EmailAlreadyUseException;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(controllers = UserController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
@AutoConfigureDataJpa
class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new UserController(userService, jwtService))
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 로그인_성공() throws Exception {
		// given
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";

		given(jwtService.getJwt(any(), eq(EnumToken.ACCESS)))
			.willReturn(accessToken);
		given(jwtService.getJwt(any(), eq(EnumToken.REFRESH)))
			.willReturn(refreshToken);
		given(userService.getUserWithAuthorities(any()))
			.willReturn(createUserDto("test1@test.com", "password", "test1"));

		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson("test1@test.com", "password");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.header()
				.string(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken))
			.andExpect(MockMvcResultMatchers.header()
				.string(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken))
			.andExpect(jsonPath("accessToken").value(accessToken))
			.andExpect(jsonPath("refreshToken").value(refreshToken));
	}

	@Test
	void 로그인_예외_이메일이_없음() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = "{"
			+ " \"password\" : \"password\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_이메일_형식이_아님() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson("test1", "password");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_이메일_길이_부족() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson("t@", "password");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_이메일_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson(
			"ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt@tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt.com",
			"password");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_패스워드가_없음() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = "{"
			+ " \"email\" : \"test1@test.com\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_패스워드_길이_부족() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson("test1@test.com", "p");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_패스워드_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = convertLoginDtoJson("test1@test.com",
			"passwordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpassword");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 로그인_예외_JSON_포맷_에러() throws Exception {
		// when
		String requestUrl = "/api/user/login";
		String content = "{"
			+ " \"email\" : \"test1@test.com\", "
			+ " \"password\" : \"password\" ";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectNotReadableException(actions);
	}

	@Test
	void 회원가입_성공() throws Exception {
		// given
		given(userService.signup(any()))
			.willReturn(
				createUserDto("test1@test.com", "password", "test1")
			);

		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com", "password", "test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		actions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("email").value("test1@test.com"))
			.andExpect(jsonPath("nickname").value("test1"))
			.andExpect(jsonPath("$.authorityDtoSet[0].authorityName")
				.value(EnumAuthority.ROLE_USER.name()));
	}

	@Test
	void 회원가입_예외_이메일_중복() throws Exception {
		// given
		given(userService.signup(any())).willThrow(
			new EmailAlreadyUseException(ErrorCode.EMAIL_DUPLICATION.getMessage())
		);

		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com", "password", "test1");
		AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(
			() -> postRequest(requestUrl, content)
		);

		// then
		o.getCause().isInstanceOf(EmailAlreadyUseException.class);
	}

	@Test
	void 회원가입_예외_이메일이_없음() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = "{"
			+ " \"password\" : \"password\", "
			+ " \"nickname\" : \"test1\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_형식이_아님() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1", "password", "test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_길이_부족() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("t@", "password", "test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_이메일_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson(
			"ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt@ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt.com",
			"password", "test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드가_없음() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = "{"
			+ " \"email\" : \"test1@test.com\", "
			+ " \"nickname\" : \"test1\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드_길이_부족() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com", "p", "test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_패스워드_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com",
			"passwordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpassword",
			"test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임이_없음() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = "{"
			+ " \"email\" : \"test1@test.com\", "
			+ " \"password\" : \"password\" "
			+ "}";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임_길이_부족() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com", "password", "t");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_닉네임_길이_초과() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = convertUserDtoJson("test1@test.com", "password",
			"test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1");
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectValidException(actions);
	}

	@Test
	void 회원가입_예외_JSON_포맷_에러() throws Exception {
		// when
		String requestUrl = "/api/user/signup";
		String content = "{"
			+ " \"email\" : \"test1@test.com\", "
			+ " \"password\" : \"password\", "
			+ " \"nickname\" : \"test1\" ";
		ResultActions actions = postRequest(requestUrl, content);

		// then
		expectNotReadableException(actions);
	}

	@Test
	void 내정보_조회_성공() throws Exception {
		// given
		given(userService.getMyUserWithAuthorities()).willReturn(
			createUserDto("test1@test.com", "password", "test1")
		);

		// when
		String requestUrl = "/api/user/myInfo";
		ResultActions actions = getRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("email").value("test1@test.com"))
			.andExpect(jsonPath("nickname").value("test1"))
			.andExpect(jsonPath("$.authorityDtoSet[0].authorityName")
				.value(EnumAuthority.ROLE_USER.name()));
	}

	@Test
	void 사용자_조회_성공() throws Exception {
		// given
		given(userService.getUserWithAuthorities(any())).willReturn(
			createUserDto("test1@test.com", "password", "test1")
		);

		// when
		String requestUrl = "/api/user/info/test1@test.com";
		ResultActions actions = getRequest(requestUrl);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("email").value("test1@test.com"))
			.andExpect(jsonPath("nickname").value("test1"))
			.andExpect(jsonPath("$.authorityDtoSet[0].authorityName")
				.value(EnumAuthority.ROLE_USER.name()));
	}

	private String convertUserDtoJson(String email, String password, String nickname) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"email\" : \"")
			.append(email)
			.append("\",")
			.append(" \"password\" : \"")
			.append(password)
			.append("\",")
			.append(" \"nickname\" : \"")
			.append(nickname)
			.append("\"")
			.append("}"));
	}

	private String convertLoginDtoJson(String email, String password) {
		return String.valueOf(new StringBuffer().append("{")
			.append(" \"email\" : \"")
			.append(email)
			.append("\",")
			.append(" \"password\" : \"")
			.append(password)
			.append("\"")
			.append("}"));
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

	private ResultActions getRequest(String requestUrl)
		throws Exception {
		return mvc.perform(
			get(requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
		);
	}

	private UserDto createUserDto(String email, String password, String nickname) {
		return UserDto.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.authorityDtoSet(
				Collections.singleton(
					AuthorityDto.builder()
						.authorityName(EnumAuthority.ROLE_USER.name())
						.build()
				)
			)
			.build();
	}
}