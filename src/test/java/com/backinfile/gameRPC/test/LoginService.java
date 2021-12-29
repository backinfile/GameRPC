package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractLoginService;

public class LoginService extends AbstractLoginService {
    @Override
    public void pulse(boolean perSec) {
    }

    @Override
    public void testRPC(TestRPCContext context) {
        Log.game.info("test rpc function executed");
        context.returns();
    }

    @Override
    public void testAdd(TestAddContext context, int a, int b) {
        context.returns(a + b);
    }
}
