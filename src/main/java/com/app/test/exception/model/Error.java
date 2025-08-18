package com.app.test.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.lang.Nullable;


@Getter
public class Error {
    private Integer errorCode;
    private String errorMessage;
    @JsonIgnore
    private String devMessage;

    public Error(InternalErrorCode internalErrorCode, @Nullable String message, @Nullable String devMessage) {
        this.errorCode = internalErrorCode.getCode();
        this.errorMessage = message != null ? message : internalErrorCode.getMessage();
        this.devMessage = devMessage;
    }

    public Error(InternalErrorCode internalErrorCode, @Nullable String message) {
        this.errorCode = internalErrorCode.getCode();
        this.errorMessage = message != null ? message : internalErrorCode.getMessage();
        this.devMessage = null;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }
}
