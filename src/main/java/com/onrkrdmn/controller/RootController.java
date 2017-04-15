package com.onrkrdmn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Onur Karaduman
 * @since 15.04.17
 */
@RestController
public class RootController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity test() {
        String s = "Hello world";
        return new ResponseEntity(s, HttpStatus.OK);
    }
}
