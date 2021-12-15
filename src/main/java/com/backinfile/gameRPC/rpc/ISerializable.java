package com.backinfile.gameRPC.rpc;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public interface ISerializable {
    public abstract void writeTo(MessagePacker packer) throws IOException;
    public abstract void readFrom(MessageUnpacker unpacker) throws IOException;
}
