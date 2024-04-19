package com.rockbitegames.util;
import org.slf4j.Logger;

public class Log {

    private Log() {
        throw new ExceptionInInitializerError("Object creation is not allowed.");
    }

    public static void error(Logger log, String msg, Object ... args) {
        log.error(msg, args);
    }

    public static void info(Logger log, String msg, Object ... args) {
        log.info(msg, args);
    }

    public static void warn(Logger log, String msg, Object ... args) {
        log.warn(msg, args);
    }
}
