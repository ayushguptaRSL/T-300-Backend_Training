package com.ayush.backendtraining;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BackendTrainingApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        assertTrue(environment.matchesProfiles("dev"), "Dev profile should be active");
    }

}
