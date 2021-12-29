package ${packagePath};

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.*;
import com.backinfile.support.timer.*;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
<#list imports as import>
import ${import};
</#list>

<#if hasComment>
/**
<#list comments as comment>
 * ${comment}
</#list>
 */
</#if>
@GameRPCGenFile
public abstract class ${serviceType} extends Port {
    public static final String PORT_ID_PREFIX = "${serviceName}Service";

    public static class M {
<#list rpcList as rpc>
        public static final int ${rpc.hashName?upper_case} = ${rpc.hashCode?c};
</#list>
    }

    protected final TimerQueue timerQueue = new TimerQueue();
    private final Timer perSecTimer = new Timer(Time2.SEC, 0);

    public ${serviceType}() {
        super(PORT_ID_PREFIX);
    }

    public ${serviceType}(String serviceId) {
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
<#list rpcList as rpc>
            case M.${rpc.hashName?upper_case}: {
                ${rpc.callString}
                break;
            }
</#list>
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

<#list rpcList as rpc>
<#if (rpc.comments?size>0)>
    /**
<#list rpc.comments as comment>
     * ${comment}
</#list>
     */
</#if>
    @RPCMethod
    public abstract void ${rpc.bodyString};

</#list>

<#list rpcList as rpc>
    protected static class ${rpc.name?cap_first}Context {
        private final Call lastInCall;

        private ${rpc.name?cap_first}Context(Call lastInCall) {
            this.lastInCall = lastInCall;
        }

        public void returns(<#list rpc.returnParams as param>${param.typeName} ${param.name}${param?has_next?then(", ","")}</#list>) {
            Call callReturn = lastInCall.newCallReturn(new Object[]{<#list rpc.returnParams as param>${param.name}${param?has_next?then(", ","")}</#list>});
            Node.Instance.handleCall(callReturn);
        }
    }

</#list>
}
