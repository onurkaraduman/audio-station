package com.onrkrdmn.validator;

import com.onrkrdmn.validator.exception.ValidateException;

/**
 * Url valdatior interface
 *
 * @author Onur Karaduman
 */
public interface UrlValidator {

    /**
     * Url validation process
     *
     * @return
     * @throws ValidateException
     */
    public boolean validate(String url) throws ValidateException;
}
