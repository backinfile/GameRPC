package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.gameRPC.gen.struct.*;

public abstract class AbstractRoomService extends Port {
    public static final String PORT_ID_PREFIX = "RoomService";

    public static class M {
        public static final int LOGIN_STRING = -1071324729;
        public static final int START_GAME = -1573540433;
        public static final int GET_HUMAN_INFO_LONG = -1381217262;
    }

    public AbstractRoomService(String serviceId) {
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
        pulse(false);
    }

    @Override
    public void casePulseAfter() {
    }

    @Override
    public void handleRequest(int requestKey, Object[] args, Object clientVar) {
        switch (requestKey) {
            case M.LOGIN_STRING: {
                login((long) clientVar, (String) args[0]);
                break;
            }
            case M.START_GAME: {
                startGame((long) clientVar);
                break;
            }
            case M.GET_HUMAN_INFO_LONG: {
                getHumanInfo((long) args[0]);
                break;
            }
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

    public abstract void enter(long humanId);


    @RPCMethod
    public abstract void login(@ClientField long id, String name);

    @RPCMethod
    public abstract void startGame(@ClientField long id);

    @RPCMethod
    public abstract void getHumanInfo(long id);

}
