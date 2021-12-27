package com.backinfile.gameRPC.genstruct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;

import java.util.*;

/**
 * 用于验证Node身份
 */
@GameRPCGenFile
public class DNodeVerify extends DSyncBase {
    public static final String TYPE_NAME = DNodeVerify.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DNodeVerify.class.getSimpleName());
    public static int FIELD_NUM = 3;

	/** player token */
	private String token;
	private List<Long> idList;
	private EType type;

    DNodeVerify() {
    }

    public static DNodeVerify.Builder newBuilder() {
        return new DNodeVerify.Builder();
    }

	/** player token */
    public String getToken() {
        return token;
    }

	/** player token */
    public boolean hasToken() {
        return _valueMap.get(0);
    }

    public int getIdListSize() {
        return idList.size();
    }

    public List<Long> getIdListList() {
        return idList;
    }

    public boolean hasIdList() {
        return _valueMap.get(1);
    }

    public EType getType() {
        return type;
    }

    public boolean hasType() {
        return _valueMap.get(2);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(token);
        out.write(idList);
        out.write(type);
    }

    @Override
    public void readFrom(InputStream in) {
        token = in.read();
        idList = Collections.unmodifiableList(in.read());
        type = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
	    /** player token */
	    private String token = "";
	    final private List<Long> idList = new ArrayList<>();
	    private EType type = EType.Int;

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public DNodeVerify build() {
            DNodeVerify _DNodeVerify = new DNodeVerify();
            _DNodeVerify._valueMap = new BitSet(FIELD_NUM);
            _DNodeVerify._valueMap.or(this._valueMap);
            _DNodeVerify.token = this.token;
            _DNodeVerify.idList = List.copyOf(this.idList);
            _DNodeVerify.type = this.type;
            return _DNodeVerify;
        }

	    /** player token */
        public void setToken(String token) {
            this.token = token;
            this._valueMap.set(0);
        }

        public void addAllIdList(List<Long> idList) {
            this.idList.addAll(idList);
            this._valueMap.set(1);
        }

        public void addIdList(long idList) {
            this.idList.add(idList);
            this._valueMap.set(1);
        }

        public void setType(EType type) {
            this.type = type;
            this._valueMap.set(2);
        }

    }
}
