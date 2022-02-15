package kdk.hometact.error.exception;

import kdk.hometact.error.ErrorCode;

public class InvalidValueException extends BusinessException {

	public InvalidValueException(String value) {
		super(value, ErrorCode.INVALID_INPUT_VALUE);
	}

	public InvalidValueException(String value, ErrorCode errorCode) {
		super(value, errorCode);
	}
}
