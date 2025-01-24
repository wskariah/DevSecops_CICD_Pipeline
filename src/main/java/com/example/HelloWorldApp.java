package com.example;

import static spark.Spark.*;  //spark Framework,

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloWorldApp {

    public static String VERSION = "@VERSION@";
    public static String GIT_COMMIT = "@GIT_COMMIT@";

    public static void main(String[] args) {
        port(8080); 

        get("/", (req, res) -> {
            // Geting the current timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            return "<h1>Hello, World!</h1>" +
                    "<p>Version: " + VERSION + "</p>" +
                    "<p>Git Commit: " + GIT_COMMIT + "</p>" +
                    "<p>Timestamp: " + timestamp + "</p>";
        });
    }
}
