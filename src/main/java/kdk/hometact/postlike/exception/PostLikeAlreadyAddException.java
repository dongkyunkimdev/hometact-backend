package kdk.hometact.postlike.exception;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.BusinessException;

public class PostLikeAlreadyAddException extends BusinessException {

	public PostLikeAlreadyAddException() {
		super(ErrorCode.POSTLIKE_DUPLICATION);
	}
}
