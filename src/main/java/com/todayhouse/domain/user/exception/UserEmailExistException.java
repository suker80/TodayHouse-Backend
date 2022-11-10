package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserEmailExistException extends BaseException {
    public UserEmailExistException() {
        super(BaseResponseStatus.POST_USER_EXISTS_EMAIL);
    }
}
