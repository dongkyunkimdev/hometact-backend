package kdk.hometact.user.exception;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.InvalidValueException;

public class NicknameAlreadyUseException extends InvalidValueException {

	public NicknameAlreadyUseException(String value) {
		super(value, ErrorCode.NICKNAME_DUPLICATION);
	}
}