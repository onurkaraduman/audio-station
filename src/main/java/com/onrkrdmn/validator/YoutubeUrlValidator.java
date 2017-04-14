package com.onrkrdmn.validator;

import com.onrkrdmn.helper.ValidateHelper;
import com.onrkrdmn.validator.exception.ValidateException;
import org.springframework.stereotype.Service;

/**
 * Youtube url validator
 *
 * @author Onur Karaduman
 */
@Service
public class YoutubeUrlValidator implements UrlValidator {

    /**
     * Validate youtube url with apache validator and custom regex validator
     *
     * @param url
     * @return
     * @throws ValidateException
     */
    @Override
    public boolean validate(String url) {
        boolean result1 = ValidateHelper.isValidUrl(url);
        boolean result2 = ValidateHelper.isValidYoutubeUrl(url);
        if (result1 && result2) {
            return true;
        }
        throw new ValidateException(url);
    }
}
