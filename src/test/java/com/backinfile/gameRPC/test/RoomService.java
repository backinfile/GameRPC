package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractRoomService;
import com.backinfile.gameRPC.gen.service.LoginServiceProxy;
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

    }
}