package com.backinfile.gameRPC;

import com.backinfile.gameRPC.serialize.ISerializable;

import java.util.BitSet;

public abstract class DSyncBase implements ISerializable {
    protected BitSet changedMap;

    public abstract static class Builder {
        protected BitSet changedMap;
    }
}
