package com.queue;

public class NetworkException extends Exception {
    public NetworkException(String response) {
        super(response);
    }
}
