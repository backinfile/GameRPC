package com.backinfile.gameRPC;


import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.serialize.SerializableManager;
import com.backinfile.gameRPC.struct.DNodeVerify;

public class JUnitTest {

    public static void main(String[] args) {
        SerializableManager.registerAll();
        testDSyncSerialize();
    }

    public static void testDSyncSerialize() {
        DNodeVerify nodeVerify;
        Call call = Call.newCall(123, new CallPoint("nodeA", "portA"), new CallPoint("nodeB", "portB"), 9088, new Object[]{});

        Call newCall = Node.serializeCall(call);

        assert true;
    }
}
