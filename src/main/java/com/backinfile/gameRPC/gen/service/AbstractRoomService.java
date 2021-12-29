package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.*;
import com.backinfile.support.timer.*;
import com.backinfile.gameRPC.gen.struct.*;

public abstract class AbstractRoomService extends Port {
    public static final String PORT_ID_PREFIX = "RoomService";

    public static class M {
        public static final int LOGIN_STRING_BOOLEAN = -1232480880;
        public static final int START_GAME = -1573540433;
        public static final int GET_HUMAN_INFO_LONG = -1381217262;
    }

    private final TimerQueue timerQueue = new TimerQueue();
    private final Timer perSecTimer = new Timer(Time2.SEC, 0);

    public AbstractRoomService() {
        super(PORT_ID_PREFIX);
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
            case M.LOGIN_STRING_BOOLEAN: {
                login(new LoginContext(from), (long) clientVar, (String) args[0], (boolean) args[1]);
                break;
            }
            case M.START_GAME: {
                startGame(new StartGameContext(from), (long) clientVar);
                break;
            }
            case M.GET_HUMAN_INFO_LONG: {
                getHumanInfo(new GetHumanInfoContext(from), (long) args[0]);
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
    public abstract void startGame(StartGameContext context, @ClientField long id);

    @RPCMethod
    public abstract void getHumanInfo(GetHumanInfoContext context, long id);


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

    protected static class StartGameContext {
        private final Call lastInCall;

        private StartGameContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns() {
            Call callReturn = lastInCall.newCallReturn(new Object[]{});
            Node.Instance.handleCall(callReturn);
        }
    }

    protected static class GetHumanInfoContext {
        private final Call lastInCall;

        private GetHumanInfoContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns(String name) {
            Call callReturn = lastInCall.newCallReturn(new Object[]{name});
            Node.Instance.handleCall(callReturn);
        }
    }

}
