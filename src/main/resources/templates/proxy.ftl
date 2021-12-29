<#macro listParams params><#list params as param>${param.typeName} ${param.name}${param?has_next?then(", ","")}</#list></#macro>
<#macro listParamNames params><#list params as param>${param.name}${param?has_next?then(", ","")}</#list></#macro>
<#macro listParamCalls params><#list params as param>r.getResult(${param?index})${param?has_next?then(", ","")}</#list></#macro>
package ${packagePath};

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
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
public class ${proxyType} {
    private final String targetNodeId;
    private final String targetPortId;

    private ${proxyType}(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static ${proxyType} newInstance(String targetNodeId, String targetPortId) {
        return new ${proxyType}(targetNodeId, targetPortId);
    }

    public static ${proxyType} newInstance(String targetPortId) {
        return new ${proxyType}(Node.Instance.getId(), targetPortId);
    }

    public static ${proxyType} newInstance() {
        return new ${proxyType}(Node.Instance.getId(), ${serviceType}.PORT_ID_PREFIX);
    }


<#list rpcList as rpc>
<#if (rpc.comments?size>0)>
    /**
<#list rpc.comments as comment>
     * ${comment}
</#list>
     */
</#if>
<#if rpc.clientVar??>
    @RPCMethod(client = true)
<#else>
    @RPCMethod
</#if>
    public ${rpc.name?cap_first}Future ${rpc.name}(<@listParams params=rpc.callParams/>) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, ${serviceType}.M.${rpc.hashName?upper_case}, new Object[]{<@listParamNames params=rpc.callParams/>});
        return new ${rpc.name?cap_first}Future(Port.getCurrentPort(), call.id);
    }

</#list>

<#list rpcList as rpc>
    @FunctionalInterface
    public interface I${rpc.name?cap_first}FutureListener {
        void onResult(<@listParams params=rpc.returnParams/>${(rpc.returnParams?size>0)?then(", ", "")}Params context);
    }

    public static class ${rpc.name?cap_first}Future {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private ${rpc.name?cap_first}Future(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public ${rpc.name?cap_first}Future context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public ${rpc.name?cap_first}Future then(I${rpc.name?cap_first}FutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(<@listParamCalls params=rpc.returnParams/>${(rpc.returnParams?size>0)?then(", ", "")}contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public ${rpc.name?cap_first}Future error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }

        /** 设置监听失效时间 */
        public ${rpc.name?cap_first}Future timeout(long timeout) {
            localPort.getTerminal().setTimeout(callId, timeout);
            return this;
        }
    }

</#list>
}

