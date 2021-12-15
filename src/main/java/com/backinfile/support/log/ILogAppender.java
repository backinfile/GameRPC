package com.backinfile.support.log;

import com.backinfile.support.Utils;

public interface ILogAppender {
    void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable);

    public static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[4].toString();
    }

    public static class ConsoleAppender implements ILogAppender {
        public static ConsoleAppender Instance = new ConsoleAppender();

        @Override
        public void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable) {
            if (args != null) {
                message = Utils.format(message, args);
            }
            System.out.println(Utils.format("{} [{}][{}] {}", getCallerInfo(), tag, logLevel.name(), message));
            if (throwable != null) {
                System.out.println(throwable.getClass().getName() + ": " + throwable.getMessage());
                for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    System.out.println("\t" + stackTraceElement.toString());
                }
            }
        }
    }

    public static class EmptyAppender implements ILogAppender {
        public static EmptyAppender Instance = new EmptyAppender();

        @Override
        public void append(LogLevel logLevel, String tag, String message, Object[] args, Throwable throwable) {
        }
    }
}
