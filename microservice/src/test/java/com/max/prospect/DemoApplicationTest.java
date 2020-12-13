package com.max.prospect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The @SpringBootTest annotation tells Spring Boot to look for a main configuration class
 * (one with @SpringBootApplication, for instance)
 * and use that to start a Spring application context.
 */
@SpringBootTest
public class DemoApplicationTest {

    @Test
    public void contextLoads() {
    }
}
