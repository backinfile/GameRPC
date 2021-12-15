package com.backinfile.gameRPC.rpc;

import java.io.IOException;
import java.util.Objects;

import com.backinfile.mrpc.serilize.ISerializable;
import com.backinfile.mrpc.serilize.InputStream;
import com.backinfile.mrpc.serilize.OutputStream;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

public class CallPoint{
	public final String nodeID;
	public final String portID;

	public CallPoint(CallPoint callPoint) {
		this.nodeID = callPoint.nodeID;
		this.portID = callPoint.portID;
	}

	public CallPoint(String nodeID, String portID) {
		this.nodeID = nodeID;
		this.portID = portID;
	}
}
