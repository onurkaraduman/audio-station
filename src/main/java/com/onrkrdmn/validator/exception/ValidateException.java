package com.onrkrdmn.validator.exception;

/**
 * Url validation exception
 *
 * @author Onur Karaduman
 * @since 29.03.17.
 */
@lombok.Getter
@lombok.Setter
public class ValidateException extends RuntimeException {
    private String url;

    public ValidateException(String url) {
        super("Url validation exception URL:" + url);
        this.url = url;
    }
}
