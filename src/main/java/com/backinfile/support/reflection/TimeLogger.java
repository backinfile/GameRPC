package com.backinfile.support.reflection;

import com.backinfile.support.Time2;
import com.backinfile.support.log.UtilLog;

public class TimeLogger {
    private long startTime;
    private String name;

    public TimeLogger(String name) {
        this.name = name;
        startTime = Time2.currentTimeMillis();
    }

    public void log() {
        UtilLog.timer.info("{} using {} second", name, (Time2.currentTimeMillis() - startTime) / (float) Time2.SEC);
    }
}
