package com.backinfile.gameRPC;

import com.backinfile.gameRPC.rpc.ISerializable;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.util.BitSet;

public abstract class DSyncBase implements ISerializable {
    protected BitSet changedMap;

    public abstract static class Builder {
        protected BitSet changedMap;
    }
}
