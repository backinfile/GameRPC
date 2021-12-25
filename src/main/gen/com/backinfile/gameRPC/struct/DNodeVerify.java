package com.backinfile.gameRPC.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;

/**
 * 用于验证Node身份
 */
@GameRPCGenFile
public class DNodeVerify extends DSyncBase {
    public static final String TYPE_NAME = DNodeVerify.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DNodeVerify.class.getSimpleName());
    public static int FIELD_NUM = 2;

    /**
     * player token
     */
    private String token;

    private List<Long> idList;


    DNodeVerify() {
    }

    public static DNodeVerify.Builder newBuilder() {
        return new DNodeVerify.Builder();
    }

    public String getToken() {
        return token;
    }

    public boolean hasToken() {
        return _changedMap.get(0);
    }

    public List<Long> getIdList() {
        return idList;
    }

    public boolean hasIdList() {
        return _changedMap.get(1);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(token);
        out.write(idList);
    }

    @Override
    public void readFrom(InputStream in) {
        token = in.read();
        idList = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
        /**
         * player token
         */
        private String token;

        private List<Long> idList;


        private Builder() {
            this._changedMap = new BitSet(FIELD_NUM);
        }

        public DNodeVerify build() {
            DNodeVerify _DNodeVerify = new DNodeVerify();
            _DNodeVerify._changedMap = new BitSet(FIELD_NUM);
            _DNodeVerify._changedMap.or(this._changedMap);
            _DNodeVerify.token = this.token;
            _DNodeVerify.idList = List.copyOf(this.idList);
            return _DNodeVerify;
        }

        public void setToken(String token) {
            this.token = token;
            this._changedMap.set(0);
        }

        public void setIdList(List<Long> idList) {
            this.idList = idList;
            this._changedMap.set(1);
        }

    }
}
