package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.serialize.ISerializable;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

public class CallPoint implements ISerializable {
    public String nodeID;
    public String portID;

    public CallPoint() {
    }

    public CallPoint(CallPoint callPoint) {
        this.nodeID = callPoint.nodeID;
        this.portID = callPoint.portID;
    }

    public CallPoint(String nodeID, String portID) {
        this.nodeID = nodeID;
        this.portID = portID;
    }

    public CallPoint copy() {
        return new CallPoint(nodeID, portID);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(nodeID);
        out.write(portID);
    }

    @Override
    public void readFrom(InputStream in) {
        nodeID = in.read();
        portID = in.read();
    }
}
