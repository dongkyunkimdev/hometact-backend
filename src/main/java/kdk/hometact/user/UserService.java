package kdk.hometact.user;

import java.util.Collections;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.auth.Authority;
import kdk.hometact.user.dto.UpdateNicknameDto;
import kdk.hometact.user.dto.UpdatePasswordDto;
import kdk.hometact.user.dto.UserDto;
import kdk.hometact.user.exception.BadPasswordException;
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
		validDuplEmail(userDto.getEmail());
		validDuplNickname(userDto.getNickname());

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

	private void validDuplEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyUseException(ErrorCode.EMAIL_DUPLICATION.getMessage());
		}
	}

	private void validDuplNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
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

	@Transactional
	public void updateNickname(UpdateNicknameDto updateNicknameDto) {
		validDuplNickname(updateNicknameDto.getNickname());
		User user = getUser();
		user.updateNickname(updateNicknameDto);
	}

	private User getUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByEmail).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
	}

	@Transactional
	public void updatePassword(UpdatePasswordDto updatePasswordDto) {
		User user = getUser();
		validEmailPassword(updatePasswordDto.getOriginalPassword(), user.getPassword());
		user.updatePassword(UpdatePasswordDto.builder()
			.newPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()))
			.build()
		);
	}

	private void validEmailPassword(String originalPassword, String password) {
		if (!passwordEncoder.matches(originalPassword, password)) {
			throw new BadPasswordException(ErrorCode.BAD_PASSWORD.getMessage());
		}
	}
}