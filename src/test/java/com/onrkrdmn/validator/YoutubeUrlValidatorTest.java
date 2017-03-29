package com.onrkrdmn.validator;

import com.onrkrdmn.helper.ValidateHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by onur on 29.03.17.
 */
public class YoutubeUrlValidatorTest {

    @Autowired
    private YoutubeUrlValidator

    @Test
    public void validate() throws Exception {
        boolean result = ValidateHelper.validateUrl("https://www.youtube.com/watch?v=JOslX79eXpk");
        boolean result2 = ValidateHelper.validateUrl("http://invalidURL^$&%$&^");
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }

}