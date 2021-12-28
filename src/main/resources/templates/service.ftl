package ${packagePath};

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.*;
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
<#list rpcList as rpc>
        public static final int ${rpc.hashName?upper_case} = ${rpc.hashCode?c};
</#list>
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
    public void handleRequest(int requestKey, Object[] args, Object clientVar) {
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

    public abstract void enter(long humanId);


<#list rpcList as rpc>
<#if rpc.clientVar??>
    @RPCMethod
    public abstract void ${rpc.name}(<#list ([rpc.clientVar] + rpc.callParams) as param>${param?is_first?then("@ClientField ","")}${param.typeName} ${param.name}${param?has_next?then(", ","")}</#list>);
<#else>
    @RPCMethod
    public abstract void ${rpc.name}(<#list rpc.callParams as param>${param.typeName} ${param.name}${param?has_next?then(", ","")}</#list>);
</#if>

</#list>
}
