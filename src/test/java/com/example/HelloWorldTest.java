package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void testGetGreeting() {
        HelloWorld hw = new HelloWorld();
        assertEquals("Hello, World!", hw.getGreeting(), "Greeting should be 'Hello, World!'");
    }
}
