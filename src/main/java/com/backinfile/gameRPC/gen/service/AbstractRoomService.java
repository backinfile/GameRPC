package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;

public abstract class AbstractRoomService extends Port {
    public static final String PORT_ID_PREFIX = "RoomService";

    public static class M {
        public static final int LOGIN_STRING_BOOLEAN = -1232480880;
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
        Call from = getTerminal().getLastInCall();
        switch (requestKey) {
            case M.LOGIN_STRING_BOOLEAN: {
                login(new LoginContext(from), (long) clientVar, (String) args[0], (boolean) args[1]);
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


    @RPCMethod
    public abstract void login(LoginContext context, @ClientField long id, String name, boolean local);

    @RPCMethod
    public abstract void startGame(@ClientField long id);

    @RPCMethod
    public abstract void getHumanInfo(long id);


    protected static class LoginContext {
        private final Call lastInCall;

        private LoginContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns(int code, String message, boolean online) {
            Call callReturn = lastInCall.newCallReturn(new Object[]{code, message, online});
            Node.Instance.handleCall(callReturn);
        }
    }

}
