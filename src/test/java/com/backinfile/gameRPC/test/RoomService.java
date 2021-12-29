package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractRoomService;
import com.backinfile.gameRPC.gen.service.LoginServiceProxy;
import com.backinfile.gameRPC.rpc.Port;

public class RoomService extends AbstractRoomService {
    @Override
    public void pulse(boolean perSec) {
        if (perSec) {
            LoginServiceProxy proxy = LoginServiceProxy.newInstance();
            proxy.testRPC().then(context -> {
                Log.game.info("rpc callback in Port:{}", Port.getCurrentPort().getId());
            });

        }
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