package kdk.hometact.user.exception;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.InvalidValueException;

public class BadPasswordException extends InvalidValueException {

	public BadPasswordException(String value) {
		super(value, ErrorCode.BAD_PASSWORD);
	}
}