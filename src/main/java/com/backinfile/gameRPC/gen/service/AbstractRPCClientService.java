package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.*;
import com.backinfile.support.timer.*;
import com.backinfile.gameRPC.gen.struct.*;

public abstract class AbstractRPCClientService extends Port {
    public static final String PORT_ID_PREFIX = "RPCClientService";

    public static class M {
    }

    protected final TimerQueue timerQueue = new TimerQueue();
    private final Timer perSecTimer = new Timer(Time2.SEC, 0);

    public AbstractRPCClientService() {
        super(PORT_ID_PREFIX);
    }

    public AbstractRPCClientService(String serviceId) {
        super(serviceId);
    }

    @Override
    public void startup() {
    }

    @Override
    public void casePulseBefore() {
    }

    @Override
    public void casePulse() {
        try {
            pulse(perSecTimer.isPeriod());
        } catch (Exception e) {
            Log.core.error("service 心跳中出错 class=" + this.getClass().getSimpleName(), e);
        }
        timerQueue.update();
    }

    @Override
    public void casePulseAfter() {
    }

    @Override
    public void handleRequest(int requestKey, Object[] args, Object clientVar) {
        Call from = getTerminal().getLastInCall();
        switch (requestKey) {
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);


}
