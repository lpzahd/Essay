package com.lpzahd.atool.error;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class FixedError extends Exception {

    private int code;
    private String message;
    private Throwable cause;

    public FixedError() {
    }

    public FixedError(int code, String message) {
        this(code, message, null);
    }

    public FixedError(int code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public static class FixedErrorHandler {

        public static final int ERROR_NULL = 0;
        public static final int ERROR_OTHER = 1;

        public static FixedError handleError(Throwable throwable) {
            if (throwable == null)
                return nullError();
            return new FixedError(ERROR_OTHER, throwable.getMessage(), throwable);
        }


        public static FixedError nullError() {
            return new FixedError(ERROR_NULL, "the throwable is null!");
        }
    }

}
