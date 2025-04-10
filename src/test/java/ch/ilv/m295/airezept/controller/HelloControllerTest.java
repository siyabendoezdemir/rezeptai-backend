package ch.ilv.m295.airezept.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {

    private final HelloController helloController = new HelloController();

    @Test
    void hello_ShouldReturnHelloWorld() {
        // Act
        String response = helloController.hello();

        // Assert
        assertEquals("Hello World", response);
    }
} 