package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractRoomService;
import com.backinfile.gameRPC.gen.service.LoginServiceProxy;
import com.backinfile.gameRPC.gen.service.RoomServiceProxy;
import com.backinfile.gameRPC.gen.struct.DHuman;
import com.backinfile.gameRPC.gen.struct.DProp;
import com.backinfile.gameRPC.rpc.Port;
import com.backinfile.support.Time2;

public class RoomService extends AbstractRoomService {

    @Override
    public void startup() {
        super.startup();

        timerQueue.applyTimer(Time2.SEC, () -> {
            LoginServiceProxy proxy = LoginServiceProxy.newInstance();
            proxy.testRPC().then(context -> {
                Log.game.info("rpc callback in Port:{}", Port.getCurrentPort().getId());
            });
        });
        timerQueue.applyTimer(Time2.SEC * 3, () -> {
            LoginServiceProxy proxy = LoginServiceProxy.newInstance();
            proxy.testAdd(123, 234).then((result, context) -> {
                Log.game.info("result = {}", result);
            });
        });

        timerQueue.applyTimer(Time2.SEC * 5, () -> {
            RoomServiceProxy proxy = RoomServiceProxy.newInstance();
            proxy.getHumanInfo(12321).then((human, context) -> {
                Log.game.info("after hash={}", human.hashCode());
                Log.game.info("id contains:{} name contains:{}", human.hasId(), human.hasName());
            });
        });
    }

    @Override
    public void pulse(boolean perSec) {
    }

    @Override
    public void login(LoginContext context, long id, String name, boolean local) {

    }

    @Override
    public void startGame(StartGameContext context, long id) {

    }

    @Override
    public void getHumanInfo(GetHumanInfoContext context, long id) {
        var builder = DHuman.newBuilder();
        builder.setId(id);
        var dProp = DProp.newBuilder();
        dProp.setPropName("health");
        dProp.setPropValue(1200d);
        builder.addProps(dProp.build());
        var dHuman = builder.build();
        context.returns(dHuman);
        Log.game.info("before hash = {}", dHuman.hashCode());
    }
}