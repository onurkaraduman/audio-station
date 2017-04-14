package com.onrkrdmn.helper;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by onur on 29.03.17.
 */
public class ValidateHelperTest {
    @Test
    public void validateUrl() throws Exception {
        boolean result = ValidateHelper.isValidUrl("https://www.youtube.com/watch?v=JOslX79eXpk");
        boolean result2 = ValidateHelper.isValidUrl("http://invalidURL^$&%$&^");
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void validateYoutubeUrl() throws Exception {
        boolean result = ValidateHelper.isValidYoutubeUrl("https://www.youtube.com/watch?v=JOslX79eXpk");
        boolean result2 = ValidateHelper.isValidYoutubeUrl("http://invalidURL^$&%$&^");
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

}