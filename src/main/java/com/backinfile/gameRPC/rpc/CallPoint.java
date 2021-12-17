package com.backinfile.gameRPC.rpc;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class CallPoint implements ISerializable {
    public String nodeID;
    public String portID;

    private CallPoint() {

    }

    public CallPoint(CallPoint callPoint) {
        this.nodeID = callPoint.nodeID;
        this.portID = callPoint.portID;
    }

    public CallPoint(String nodeID, String portID) {
        this.nodeID = nodeID;
        this.portID = portID;
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        packer.packString(nodeID);
        packer.packString(portID);
    }

    @Override
    public void readFrom(MessageUnpacker unpacker) throws IOException {
        nodeID = unpacker.unpackString();
        portID = unpacker.unpackString();
    }

    public static void pack(CallPoint callPoint, MessagePacker packer) throws IOException {
        callPoint.writeTo(packer);
    }

    public static CallPoint unpack(MessageUnpacker unpacker) throws IOException {
        CallPoint callPoint = new CallPoint();
        callPoint.readFrom(unpacker);
        return callPoint;
    }
}
