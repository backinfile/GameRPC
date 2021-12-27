package ${packagePath};

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.Port;
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
public abstract class ${serviceType} extends Port {
    public static final String PORT_ID_PREFIX = "${serviceName}Service";

    public static class M {
        public static final int ENTER_LONG = 0;
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
        pulse(false);
    }

    @Override
    public void casePulseAfter() {
    }

    @Override
    public void handleRequest(int requestKey, Object[] args, boolean fromClient) {
        switch (requestKey) {
            case M.ENTER_LONG:
                enter((long) args[0]);
                break;
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

    public abstract void enter(long humanId);


<#list rpcList as rpc>
    ${rpc.client?if_exists }
    public abstract void ${rpc.name}(<#list ([rpc.clientVar] + rpc.callParams) as param>${param.typeName} ${param.name}${param?has_next?string(", ","")}</#list>);
</#list>
}
