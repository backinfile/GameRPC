package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.*;
import com.backinfile.support.timer.*;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

/**
 * 登陆服务，当使用客户端-服务器模式时，需要在服务器启用一个登陆服务
 * 当接受到客户端的消息时，发来的call在此service处理, 进行验证
 * 验证完成后，此service将call推送到服务器node上
 */
@GameRPCGenFile
public abstract class AbstractLoginService extends Port {
    public static final String PORT_ID_PREFIX = "LoginService";

    public static class M {
        public static final int VERIFY = -819951495;
        public static final int HEART_BEAT = 1929975823;
        public static final int TEST_ADD_INTEGER_INTEGER = -100262862;
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
            case M.VERIFY: {
                if (clientVar != null) {
                    verify(new VerifyContext(from), (String) clientVar);
                }
                break;
            }
            case M.HEART_BEAT: {
                if (clientVar != null) {
                    heartBeat(new HeartBeatContext(from), (String) clientVar);
                }
                break;
            }
            case M.TEST_ADD_INTEGER_INTEGER: {
                if (clientVar != null) {
                    testAdd(new TestAddContext(from), (String) clientVar, (int) args[0], (int) args[1]);
                }
                break;
            }
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

    /**
     * 客户端连接成功后立即发送 仅用于身份验证
     */
    @RPCMethod
    public abstract void verify(VerifyContext context, @ClientField String token);

    /**
     * 心跳 用于保持连接
     */
    @RPCMethod
    public abstract void heartBeat(HeartBeatContext context, @ClientField String token);

    @RPCMethod
    public abstract void testAdd(TestAddContext context, @ClientField String token, int a, int b);


    protected static class VerifyContext {
        private final Call lastInCall;

        private VerifyContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns() {
            Call callReturn = lastInCall.newCallReturn(new Object[]{});
            Node.Instance.handleCall(callReturn);
        }
    }

    protected static class HeartBeatContext {
        private final Call lastInCall;

        private HeartBeatContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns() {
            Call callReturn = lastInCall.newCallReturn(new Object[]{});
            Node.Instance.handleCall(callReturn);
        }
    }

    protected static class TestAddContext {
        private final Call lastInCall;

        private TestAddContext(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns(int result) {
            Call callReturn = lastInCall.newCallReturn(new Object[]{result});
            Node.Instance.handleCall(callReturn);
        }
    }

}
