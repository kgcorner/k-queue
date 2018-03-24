package com.kqueue.demo;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resource {
    private static final Logger LOGGER = Logger.getLogger(Resource.class);
    @PostMapping("receive")
    public String  receive(@RequestBody String data) {
        LOGGER.info("received event");
        return data;
    }
}
