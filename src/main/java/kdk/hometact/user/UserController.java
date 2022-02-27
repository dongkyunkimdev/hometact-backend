package kdk.hometact.user;

import javax.validation.Valid;
import kdk.hometact.security.jwt.EnumToken;
import kdk.hometact.security.jwt.JwtFilter;
import kdk.hometact.security.jwt.JwtService;
import kdk.hometact.security.jwt.dto.TokenDto;
import kdk.hometact.user.dto.LoginDto;
import kdk.hometact.user.dto.UpdateNicknameDto;
import kdk.hometact.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
		String accessToken = jwtService.getJwt(loginDto, EnumToken.ACCESS);
		String refreshToken = jwtService.getJwt(loginDto, EnumToken.REFRESH);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
		httpHeaders.add(JwtFilter.REFRESH_HEADER, "Bearer " + refreshToken);
		UserDto userDto = userService.getUserWithAuthorities(loginDto.getEmail());

		return ResponseEntity.ok()
			.headers(httpHeaders)
			.body(TokenDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.nickname(userDto.getNickname())
				.build()
			);
	}

	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(userDto));
	}

	@GetMapping("/myInfo")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<UserDto> getMyUserInfo() {
		return ResponseEntity.ok(userService.getMyUserWithAuthorities());
	}

	@GetMapping("/info/{username}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserWithAuthorities(username));
	}

	@PatchMapping("/updateNickname")
	public ResponseEntity updateUserNickname(
		@Valid @RequestBody UpdateNicknameDto updateNicknameDto) {
		userService.updateNickname(updateNicknameDto);
		return ResponseEntity.ok().build();
	}
}