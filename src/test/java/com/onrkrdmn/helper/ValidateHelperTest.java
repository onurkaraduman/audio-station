package com.onrkrdmn.helper;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by onur on 29.03.17.
 */
public class ValidateHelperTest {
    @Test
    public void validateUrl() throws Exception {
        boolean result = ValidateHelper.validateUrl("https://www.youtube.com/watch?v=JOslX79eXpk");
        boolean result2 = ValidateHelper.validateUrl("http://invalidURL^$&%$&^");
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void validateYoutubeUrl() throws Exception {
        boolean result = ValidateHelper.validateYoutubeUrl("https://www.youtube.com/watch?v=JOslX79eXpk");
        boolean result2 = ValidateHelper.validateYoutubeUrl("http://invalidURL^$&%$&^");
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

}