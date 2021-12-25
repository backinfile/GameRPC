package com.backinfile.gameRPC.test;


import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.struct.DNodeVerify;

public class JUnitTest {

    public static void main(String[] args) {
        testDSyncSerialize();
    }

    public static void testDSyncSerialize() {
        DNodeVerify.Builder builder = DNodeVerify.newBuilder();
        builder.addIdList(1234L);
        builder.setToken("playerToken");
        DNodeVerify dNodeVerify = builder.build();

        Call call = Call.newCall(123, new CallPoint("nodeA", "portA"), new CallPoint("nodeB", "portB"), 9088, new Object[]{dNodeVerify});


        Call newCall = Node.serializeCall(call);

        assert true;
    }
}
