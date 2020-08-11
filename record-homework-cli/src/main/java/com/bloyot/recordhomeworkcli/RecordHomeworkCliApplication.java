package com.bloyot.recordhomeworkcli;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecordHomeworkCliApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(RecordHomeworkCliApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("I'm running!");
    }

}
