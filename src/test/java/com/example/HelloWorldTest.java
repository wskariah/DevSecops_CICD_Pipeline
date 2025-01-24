package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldAppTest {

    private Thread sparkThread;

    @BeforeEach
    void setUp() {
        sparkThread = new Thread(() -> {
            HelloWorldApp.main(new String[]{});
        });
        sparkThread.start();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    void tearDown() {

        if (sparkThread != null) {
            sparkThread.interrupt();
        }
    }

    @Test
    void testMainOutput() throws Exception {
        // Send a GET request to the running server
        URL url = new URL("http://localhost:8080/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String output = response.toString();

        assertTrue(output.contains("<h1>Hello, World!</h1>"));
        assertTrue(output.contains("<p>Version: 1.0.0</p>"));
        assertTrue(output.contains("<p>Git Commit: abc123</p>"));
        assertTrue(output.contains("<p>Timestamp:"));

        String timestampPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timestampPattern);
        try {
            String timestamp = output.substring(output.indexOf("Timestamp:") + 10).trim();
            sdf.parse(timestamp);
        } catch (Exception e) {
            fail("Timestamp is not in the correct format");
        }
    }

    @Test
    void testTimestampFormat() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
}
