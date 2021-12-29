package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractClientService;
import com.backinfile.gameRPC.gen.service.AbstractLoginService;
import com.backinfile.gameRPC.gen.service.LoginServiceProxy;
import com.backinfile.support.Time2;

public class ClientService extends AbstractClientService {

    @Override
    public void startup() {
        super.startup();

        timerQueue.applyTimer(Time2.SEC * 5, () -> {
            var proxy = LoginServiceProxy.newInstance("server", AbstractLoginService.PORT_ID_PREFIX);
            proxy.verify();
            proxy.heartBeat().then(context -> {
                Log.client.info("heartBeat callback");
            });
        });
    }

    @Override
    public void pulse(boolean perSec) {

    }
}
