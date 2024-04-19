package com.rockbitegames.util;

import com.rockbitegames.exception.OptionalExceptionHandler;

import java.util.Optional;

public class GetOptionalValue {
    private GetOptionalValue() {}

    public static <T> T getOptional(Optional<T> value) throws OptionalExceptionHandler {
            if (value.isPresent()) {
                return  value.get();
            } else {
                throw new OptionalExceptionHandler("Object does not exist.");
            }
    }
}
