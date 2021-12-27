package com.backinfile.gameRPC.test;


import com.backinfile.gameRPC.gen.struct.DNodeVerify;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.serialize.SerializableManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JUnitTest {

    @BeforeEach
    public void before() {
        SerializableManager.registerAll();
    }

    @Test
    public void testDSyncSerialize() {
        DNodeVerify.Builder builder = DNodeVerify.newBuilder();
        builder.addIdList(1234L);
        builder.setToken("playerToken");
        DNodeVerify dNodeVerify = builder.build();

        Call call = Call.newCall(123, new CallPoint("nodeA", "portA"),
                new CallPoint("nodeB", "portB"), 9088, new Object[]{dNodeVerify});
        Call newCall = Node.serializeCall(call);

        assert newCall.id == call.id;
        assert newCall.args[0] instanceof DNodeVerify;
        assert ((DNodeVerify) newCall.args[0]).getToken().equals(dNodeVerify.getToken());
    }

}
