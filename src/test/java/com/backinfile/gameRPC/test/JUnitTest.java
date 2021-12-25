package com.backinfile.gameRPC.test;


import com.backinfile.gameRPC.gen.BaseGenerator;
import com.backinfile.gameRPC.gen.struct.DNodeVerify;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.serialize.SerializableManager;

public class JUnitTest {

    public static void main(String[] args) {
        testDSyncSerialize();
    }

    public static void testDSyncSerialize() {
        SerializableManager.registerAll(BaseGenerator.class.getClassLoader(), JUnitTest.class.getClassLoader());
        DNodeVerify.Builder builder = DNodeVerify.newBuilder();
        builder.addIdList(1234L);
        builder.setToken("playerToken");
        DNodeVerify dNodeVerify = builder.build();

        Call call = Call.newCall(123, new CallPoint("nodeA", "portA"), new CallPoint("nodeB", "portB"), 9088, new Object[]{dNodeVerify});


        Call newCall = Node.serializeCall(call);

        System.out.println();
        assert true;
    }
}
