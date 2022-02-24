package kdk.hometact.user;

import java.util.Collections;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.auth.Authority;
import kdk.hometact.user.dto.UserDto;
import kdk.hometact.user.exception.EmailAlreadyUseException;
import kdk.hometact.user.exception.NicknameAlreadyUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserDto signup(UserDto userDto) {
		validDuplEmail(userDto);
		validDuplNickname(userDto);

		User user = toEntity(userDto);

		return UserDto.from(userRepository.save(user));
	}

	private User toEntity(UserDto userDto) {
		return User.builder()
			.email(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword()))
			.nickname(userDto.getNickname())
			.authorities(Collections.singleton(Authority.createUserRole()))
			.build();
	}

	private void validDuplEmail(UserDto userDto) {
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new EmailAlreadyUseException(ErrorCode.EMAIL_DUPLICATION.getMessage());
		}
	}

	private void validDuplNickname(UserDto userDto) {
		if (userRepository.existsByNickname(userDto.getNickname())) {
			throw new NicknameAlreadyUseException(ErrorCode.EMAIL_DUPLICATION.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public UserDto getUserWithAuthorities(String email) {
		return UserDto.from(userRepository.findOneWithAuthoritiesByEmail(email).orElseThrow(
			() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
		);
	}

	@Transactional(readOnly = true)
	public UserDto getMyUserWithAuthorities() {
		return UserDto.from(SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByEmail).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()))
		);
	}

}