package com.backinfile.support.timer;

import com.backinfile.support.func.Action0;
import com.backinfile.support.func.Function0;
import com.backinfile.support.log.UtilLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimerQueue {
    private Map<Long, TimeEvent> timers = new HashMap<>();
    private Function0<Long> getTimeFunc = null;
    private long idMax = 0;

    private static class TimeEvent {
        public Timer timer;
        public Action0 action;
    }

    public TimerQueue() {
        this(null);
    }

    public TimerQueue(Function0<Long> getTimeFunc) {
        this.getTimeFunc = getTimeFunc;
    }

    public long applyTimer(long delay, Action0 action) {
        return applyTimer(-1, delay, action);
    }

    public long applyTimer(long interval, long delay, Action0 action) {
        Timer timer = new Timer(interval, delay, getTimeFunc);
        TimeEvent timeEvent = new TimeEvent();
        timeEvent.action = action;
        timeEvent.timer = timer;
        long id = applyId();
        timers.put(id, timeEvent);
        return id;
    }

    private long applyId() {
        return idMax++;
    }

    public void removeTimer(long timerId) {
        timers.remove(timerId);
    }

    public void clearTimer() {
        timers.clear();
    }

    public void update() {
        for (long id : new ArrayList<>(timers.keySet())) {
            TimeEvent timeEvent = timers.get(id);
            if (timeEvent == null) {
                continue;
            }
            if (timeEvent.timer.isPeriod()) {
                try {
                    timeEvent.action.invoke();
                } catch (Exception e) {
                    UtilLog.core.error("error in timeQueue", e);
                }
                if (!timeEvent.timer.isRunning()) {
                    timers.remove(id);
                }
            }
        }

    }

}
