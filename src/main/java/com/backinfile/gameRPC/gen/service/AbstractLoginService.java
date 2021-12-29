package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.*;
import com.backinfile.support.timer.*;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

/**
 * 登陆服务，当使用客户端-服务器模式时，需要在服务器启用一个登陆服务
 * 当服务器node接受到客户端的消息时，把发来的call转移至此service处理, 进行验证
 * 验证完成后，此service将call推送到服务器node上
 * 客户端标志为long
 */
@GameRPCGenFile
public abstract class AbstractLoginService extends Port {
    public static final String PORT_ID_PREFIX = "LoginService";

    public static class M {
        public static final int LOGIN_STRING = -1071324729;
    }

    protected final TimerQueue timerQueue = new TimerQueue();
    private final Timer perSecTimer = new Timer(Time2.SEC, 0);

    public AbstractLoginService() {
        super(PORT_ID_PREFIX);
    }

    public AbstractLoginService(String serviceId) {
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
            case M.LOGIN_STRING: {
                login(new LoginContext(from), (String) args[0]);
                break;
            }
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

    @RPCMethod
    public abstract void login(LoginContext context, String token);


    protected static class LoginContext {
        private final Call lastInCall;

        private LoginContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns(long id) {
            Call callReturn = lastInCall.newCallReturn(new Object[]{id});
            Node.Instance.handleCall(callReturn);
        }
    }

}
