package edu.ustb.seeker.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@ComponentScan("edu.ustb.seeker")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
