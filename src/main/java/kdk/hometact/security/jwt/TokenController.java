package kdk.hometact.security.jwt;

import javax.servlet.http.HttpServletRequest;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.security.jwt.dto.TokenDto;
import kdk.hometact.security.jwt.exception.InvalidTokenException;
import kdk.hometact.user.UserService;
import kdk.hometact.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Log4j2
public class TokenController {

	private final TokenProvider tokenProvider;
	private final UserService userService;

	@GetMapping("/refresh")
	public ResponseEntity<TokenDto> refreshToken(HttpServletRequest request) {
		String refreshToken = request.getHeader(JwtFilter.REFRESH_HEADER);
		if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
			refreshToken = refreshToken.substring(7);
		}
		if (!StringUtils.hasText(refreshToken) || !tokenProvider.validateToken(request, refreshToken)) {
			throw new InvalidTokenException(ErrorCode.INVALID_TOKEN.getMessage());
		}

		UserDto userDto = userService.getUserWithAuthorities(tokenProvider.getSubject(refreshToken));
		String accessToken = tokenProvider.createToken(userDto, EnumToken.ACCESS);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
		httpHeaders.add(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken);

		return ResponseEntity.ok()
			.headers(httpHeaders)
			.body(TokenDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build()
			);

	}
}
