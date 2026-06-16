package com.cogna.core.controller;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {

    @Test
    void hello_and_health_returnValues() {
        HelloController ctrl = new HelloController();
        String h = ctrl.hello();
        assertNotNull(h);

        String health = ctrl.health();
        assertTrue(health.startsWith("OK - "));
    }
}
