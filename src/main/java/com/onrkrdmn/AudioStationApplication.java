package com.onrkrdmn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AudioStationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AudioStationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("********************************************");
        System.out.println("*      audio-station Streaming server      *");
        System.out.println("*      developed by Onur Karaduman         *");
        System.out.println("********************************************");
    }
}
