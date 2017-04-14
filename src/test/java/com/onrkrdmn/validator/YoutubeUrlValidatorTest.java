package com.onrkrdmn.validator;

import com.onrkrdmn.validator.exception.ValidateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by onur on 29.03.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class YoutubeUrlValidatorTest {

    @Autowired
    @Qualifier("youtubeUrlValidator")
    private UrlValidator youtubeUrlValidator;

    @Test
    public void validate() throws Exception {
        //https://www.youtube.com/watch?v=bCGmUCDj4Nc
        boolean result = youtubeUrlValidator.validate("https://www.youtube.com/watch?v=JOslX79eXpk");
        assertThat(result).isTrue();
    }

    @Test
    public void validateWrongUrl() {
        try {
            youtubeUrlValidator.validate("http://invalidURL^$&%$&^");
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(ValidateException.class);
        }
    }

}