package edu.ustb.seeker.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ustb.seeker.model.test.GreetingResponse;

@RestController
public class Test {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/test/greetings")
    public GreetingResponse greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new GreetingResponse(counter.incrementAndGet(),
                String.format(template, name));
    }
}
