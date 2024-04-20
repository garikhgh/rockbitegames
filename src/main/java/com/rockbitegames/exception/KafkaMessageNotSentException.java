package com.rockbitegames.exception;

public class KafkaMessageNotSentException extends RuntimeException {

    public KafkaMessageNotSentException(String msg) {
        super(msg);
    }
}
