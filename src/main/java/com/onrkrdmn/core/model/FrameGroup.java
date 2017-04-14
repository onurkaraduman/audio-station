package com.onrkrdmn.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Audio or video frame
 *
 * @author Onur Karaduman
 * @since 02.04.17
 */
@Getter
@Setter
@AllArgsConstructor
public class FrameGroup {
    private int index;
    private byte[] bytes;
    private int offset;
    private int length;
}
