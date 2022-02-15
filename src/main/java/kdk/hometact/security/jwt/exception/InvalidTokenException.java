package kdk.hometact.security.jwt.exception;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.InvalidValueException;

public class InvalidTokenException extends InvalidValueException {

	public InvalidTokenException(String value) {
		super(value, ErrorCode.INVALID_TOKEN);
	}
}
