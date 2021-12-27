package ${packagePath};

import com.backinfile.gameRPC.rpc.Port;
<#list imports as import>
import ${import};
</#list>

public abstract class Abstract${serviceName}Service extends Port {
    public static final String PORT_ID_PREFIX = "${serviceName}Service";

    public static class M {
        public static final int ENTER_LONG = 0;
    }

    public Abstract${serviceName}Service(String serviceId) {
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
}
