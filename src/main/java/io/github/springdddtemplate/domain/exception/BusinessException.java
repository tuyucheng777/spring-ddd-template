package io.github.springdddtemplate.domain.exception;

import lombok.Getter;

/// Business exception carrying a structured ErrorCode.
/// Allows domain and application layers to throw semantically rich errors
/// that the interface layer can uniformly handle via global exception handler.
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    // Convenience factory methods using sealed ErrorCode records
    public static BusinessException notFound(String code, String message) {
        return new BusinessException(new ErrorCode.NotFoundError(code, message));
    }

    public static BusinessException validation(String code, String message) {
        return new BusinessException(new ErrorCode.ValidationError(code, message));
    }

    public static BusinessException auth(String code, String message) {
        return new BusinessException(new ErrorCode.AuthError(code, message));
    }

    public static BusinessException business(String code, String message) {
        return new BusinessException(new ErrorCode.BusinessError(code, message));
    }
}
