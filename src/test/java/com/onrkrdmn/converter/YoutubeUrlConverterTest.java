package com.onrkrdmn.converter;

import com.onrkrdmn.converter.model.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by onur on 30.03.17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class YoutubeUrlConverterTest {

    @Autowired
    @Qualifier("youtubeUrlConverter")
    private UrlConverter urlConverter;

    @Test
    public void convert() throws Exception {
        // Shortest video urls
//        https://www.youtube.com/watch?v=wGyUP4AlZ6I
//    "https://www.youtube.com/watch?v=SV1sF1oukig"
//        "https://www.youtube.com/watch?v=wGyUP4AlZ6I"
        List<Video> videos = urlConverter.convert("https://www.youtube.com/watch?v=wGyUP4AlZ6I");
        assertThat(videos).size().isGreaterThan(0);
    }

}