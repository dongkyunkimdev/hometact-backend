package kdk.hometact.user.exception;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.InvalidValueException;

public class EmailAlreadyUseException extends InvalidValueException {

	public EmailAlreadyUseException(String value) {
		super(value, ErrorCode.EMAIL_DUPLICATION);
	}
}