package com.backinfile.support.log;

public class Logger {
    private String tag;

    public Logger(String tag) {
        this.tag = tag.toUpperCase();
    }

    public void info(String message, Object... args) {
        LogManager.localAppender.append(LogLevel.INFO, tag, message, args, null);
    }

    public void warn(String message, Object... args) {
        LogManager.localAppender.append(LogLevel.WARN, tag, message, args, null);
    }

    public void error(String message, Object... args) {
        LogManager.localAppender.append(LogLevel.ERROR, tag, message, args, null);
    }

    public void error(Throwable throwable, String message, Object... args) {
        LogManager.localAppender.append(LogLevel.ERROR, tag, message, args, throwable);
    }

    public void error(Throwable throwable) {
        LogManager.localAppender.append(LogLevel.ERROR, tag, "", null, throwable);
    }

    public void error(String message, Throwable throwable) {
        LogManager.localAppender.append(LogLevel.ERROR, tag, message, null, throwable);
    }
}
