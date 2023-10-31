package com.example.cliente;

public class Message {
    private String sender;
    private String receiver;
    private String message;

    private boolean isEncrypted;

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isEncrypted = false;
    }
}