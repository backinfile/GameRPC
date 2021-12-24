package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.BitSet;
import java.util.Objects;

public class DHuman extends DSyncBase {
    public static final String TYPE_NAME = DHuman.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DHuman.class.getSimpleName());
    public static int FILED_NUM = 2;

    private long id;
    private String name;

    DHuman() {
    }

    public static DHuman.Builder newBuilder() {
        return new DHuman.Builder();
    }

    public boolean hasId() {
        return _changedMap.get(0);
    }

    public long getId() {
        return id;
    }

    public boolean hasName() {
        return _changedMap.get(1);
    }

    public String getName() {
        return name;
    }


    @Override
    public void writeTo(OutputStream out) {
        out.write(id);
        out.write(name);
    }

    @Override
    public void readFrom(InputStream in) {
        id = in.read();
        name = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
        private long id = 0;
        private String name = null;

        private Builder() {
            this._changedMap = new BitSet(FILED_NUM);
        }

        public DHuman build() {
            DHuman dHuman = new DHuman();
            dHuman._changedMap = new BitSet(FILED_NUM);
            dHuman._changedMap.or(this._changedMap);
            dHuman.id = this.id;
            dHuman.name = this.name;
            return dHuman;
        }

        public void setId(long id) {
            this.id = id;
            this._changedMap.set(0);
        }

        public void setName(String name) {
            this.name = name;
            this._changedMap.set(1);
        }
    }
}
