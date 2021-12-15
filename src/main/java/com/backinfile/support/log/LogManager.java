package com.backinfile.support.log;

public class LogManager {
    static ILogAppender localAppender = ILogAppender.ConsoleAppender.Instance;

    public static Logger getLogger(String tag) {
        return new Logger(tag);
    }

    public static void setLocalAppender(ILogAppender appender) {
        LogManager.localAppender = appender;
    }
}

