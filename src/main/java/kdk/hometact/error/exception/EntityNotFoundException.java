package kdk.hometact.error.exception;

import kdk.hometact.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {

	public EntityNotFoundException(String message) {
		super(message, ErrorCode.ENTITY_NOT_FOUND);
	}
}
