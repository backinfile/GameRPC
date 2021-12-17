package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.BitSet;

public class DHuman extends DSyncBase {
    public static final String TYPE_NAME = "DHuman";
    public static final int TYPE_ID = 1;

    private long id;
    private String name;

    DHuman() {
    }

    private DHuman(DHuman.Builder builder) {
        this.changedMap = BitSet.valueOf(builder.getChangeMap().toByteArray());
        this.id = builder.id;
        this.name = builder.name;
    }

    public static DHuman.Builder newBuilder() {
        return new DHuman.Builder();
    }

    public static DHuman.Builder parseFrom(DHuman dHuman) {
        return new DHuman.Builder(dHuman);
    }

    public static DHuman.Builder parseFrom(DHuman.Builder builder) {
        return new DHuman.Builder(builder);
    }

    public boolean hasId() {
        return changedMap.get(0);
    }

    public long getId() {
        return id;
    }

    public boolean hasName() {
        return changedMap.get(1);
    }

    public String getName() {
        return name;
    }


    BitSet getChangeMap() {
        return changedMap;
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
            changedMap = new BitSet(2);
        }

        private Builder(DHuman dHuman) {
            this.changedMap = new BitSet(2);
            changedMap.or(dHuman.getChangeMap());
            this.id = dHuman.id;
            this.name = dHuman.name;
        }

        private Builder(DHuman.Builder builder) {
            this.changedMap = new BitSet(2);
            changedMap.or(builder.getChangeMap());
            this.id = builder.id;
            this.name = builder.name;
        }

        public DHuman build() {
            return new DHuman(this);
        }

        public void setId(long id) {
            this.id = id;
            changedMap.set(0);
        }

        public void setName(String name) {
            this.name = name;
            changedMap.set(1);
        }

        BitSet getChangeMap() {
            return changedMap;
        }
    }
}
