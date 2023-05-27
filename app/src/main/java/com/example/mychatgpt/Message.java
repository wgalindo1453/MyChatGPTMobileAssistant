package com.example.mychatgpt;

public class Message {

    private String text;
    private String timestamp;

    public Message(String text, String timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
