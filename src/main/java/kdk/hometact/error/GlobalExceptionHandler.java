package kdk.hometact.error;

import kdk.hometact.error.exception.BusinessException;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.postlike.exception.PostLikeAlreadyAddException;
import kdk.hometact.security.jwt.exception.InvalidTokenException;
import kdk.hometact.user.exception.BadPasswordException;
import kdk.hometact.user.exception.EmailAlreadyUseException;
import kdk.hometact.user.exception.NicknameAlreadyUseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

	// Common

	/**
	 * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다. HttpMessageConverter 에서 등록한
	 * HttpMessageConverter binding 못할경우 발생 주로 @RequestBody, @RequestPart 어노테이션에서 발생
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.error("handleMethodArgumentNotValidException", e);
		final ErrorResponse response = ErrorResponse
			.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(
		IllegalArgumentException e) {
		log.error("handleIllegalArgumentException", e);
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		log.error("handleMissingServletRequestParameterException", e);
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	/**
	 * @ModelAttribute 으로 binding error 발생시 BindException 발생한다. ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
	 */
	@ExceptionHandler(BindException.class)
	protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		log.error("handleBindException", e);
		final ErrorResponse response = ErrorResponse
			.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * enum type 일치하지 않아 binding 못할 경우 발생 주로 @RequestParam enum으로 binding 못했을 경우 발생
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error("handleMethodArgumentTypeMismatchException", e);
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e) {
		log.error("handleHttpMessageNotReadableException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.MESSAGE_NOT_READABLE);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.MESSAGE_NOT_READABLE.getStatus()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(
		EntityNotFoundException e) {
		log.error("handleEntityNotFoundException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.ENTITY_NOT_FOUND.getStatus()));
	}

	/**
	 * 지원하지 않은 HTTP method 호출 할 경우 발생
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e) {
		log.error("handleHttpRequestMethodNotSupportedException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		log.error("handleAccessDeniedException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.HANDLE_ACCESS_DENIED);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.HANDLE_ACCESS_DENIED.getStatus()));
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
		log.error("handleBusinessException", e);
		final ErrorCode errorCode = e.getErrorCode();
		final ErrorResponse response = ErrorResponse.of(errorCode);
		return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("handleException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// User
	@ExceptionHandler(EmailAlreadyUseException.class)
	protected ResponseEntity<ErrorResponse> handleEmailAlreadyUseException(
		EmailAlreadyUseException e) {
		log.error("handleEmailAlreadyUseException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.EMAIL_DUPLICATION);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.EMAIL_DUPLICATION.getStatus()));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
		UsernameNotFoundException e) {
		log.error("handleUsernameNotFoundException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.USER_NOT_FOUND.getStatus()));
	}

	@ExceptionHandler(NicknameAlreadyUseException.class)
	protected ResponseEntity<ErrorResponse> handleEmailAlreadyUseException(
		NicknameAlreadyUseException e) {
		log.error("handleNicknameAlreadyUseException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NICKNAME_DUPLICATION);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.NICKNAME_DUPLICATION.getStatus()));
	}

	@ExceptionHandler(BadPasswordException.class)
	protected ResponseEntity<ErrorResponse> handleBadPasswordException(
		BadPasswordException e) {
		log.error("handleBadPasswordException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_PASSWORD);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.BAD_PASSWORD.getStatus()));
	}

	// Authentication
	@ExceptionHandler(InvalidTokenException.class)
	protected ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(
		InvalidTokenException e) {
		log.error("handleInvalidRefreshTokenException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.INVALID_TOKEN.getStatus()));
	}

	// Login
	@ExceptionHandler(BadCredentialsException.class)
	protected ResponseEntity<ErrorResponse> handleBadCredentialsException(
		BadCredentialsException e) {
		log.error("handleBadCredentialsException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_ACCOUNT);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.INVALID_ACCOUNT.getStatus()));
	}

	// PostLike
	@ExceptionHandler(PostLikeAlreadyAddException.class)
	protected ResponseEntity<ErrorResponse> handlePostLikeAlreadyAddException(
		PostLikeAlreadyAddException e) {
		log.error("handlePostLikeAlreadyAddException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.POSTLIKE_DUPLICATION);
		return new ResponseEntity<>(response,
			HttpStatus.valueOf(ErrorCode.POSTLIKE_DUPLICATION.getStatus()));
	}


}
