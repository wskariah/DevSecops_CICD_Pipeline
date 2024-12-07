package com.example;

public class HelloWorld {
    public String getGreeting() {
        return "Hello, World!";
    }

    public static void main(String[] args) {
        HelloWorld hw = new HelloWorld();
        System.out.println(hw.getGreeting());
    }
}
