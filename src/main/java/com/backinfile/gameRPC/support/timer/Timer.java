package com.backinfile.gameRPC.support.timer;

import com.backinfile.gameRPC.support.Time2;
import com.backinfile.gameRPC.support.func.Function0;

public class Timer {
    private long interval = -1;
    private long timeout = -1;
    private final Function0<Long> getTimeFunc;

    public Timer(long delay) {
        this(-1, delay, null);
    }

    public Timer(long interval, long delay) {
        this(interval, delay, null);

    }

    public Timer(long interval, long delay, Function0<Long> getTimeFunc) {
        this.interval = interval;
        this.getTimeFunc = getTimeFunc == null ? Time2::currentTimeMillis : getTimeFunc;
        this.timeout = this.getTimeFunc.invoke() + delay;
    }

    public boolean isRunning() {
        return timeout >= 0;
    }

    public boolean isPeriod() {
        if (timeout < 0) {
            return false;
        }

        long time = getTimeFunc.invoke();
        if (time >= timeout) {
            if (interval > 0) {
                timeout += interval;
            } else {
                timeout = -1;
            }
            return true;
        }
        return false;
    }

}
