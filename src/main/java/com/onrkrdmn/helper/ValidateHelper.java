package com.onrkrdmn.helper;

import org.apache.commons.validator.UrlValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use this class for pattern validation
 * Created by onur on 29.03.17.
 */
public class ValidateHelper {

    /**
     * Validate the url with apache validator library
     *
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        return new UrlValidator().isValid(url);
    }

    /**
     * Youtube url validator
     * Regex : (?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*
     * e.g. http://www.youtube.com/embed/Woq5iX9XQhA?html5=1 return true
     *
     * @param url
     * @return
     */
    public static boolean isValidYoutubeUrl(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        return matcher.find();
    }
}
