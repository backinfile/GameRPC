package com.backinfile.gameRPC.gen.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;

import java.util.*;

@GameRPCGenFile
public class DNodeVerify extends DSyncBase {
    public static final String TYPE_NAME = DNodeVerify.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DNodeVerify.class.getSimpleName());
    public static int FIELD_NUM = 2;

	/** player token */
	private String token;
	private List<Long> idList;

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

    @Override
    public void writeTo(OutputStream out) {
        out.write(_valueMap.toLongArray());
        out.write(token);
        out.write(idList);
    }

    @Override
    public void readFrom(InputStream in) {
        _valueMap = BitSet.valueOf((long[]) in.read());
        token = in.read();
        idList = Collections.unmodifiableList(in.read());
    }

    public static class Builder extends DSyncBase.Builder {
	    /** player token */
	    private String token = "";
	    final private List<Long> idList = new ArrayList<>();

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public DNodeVerify build() {
            DNodeVerify _DNodeVerify = new DNodeVerify();
            _DNodeVerify._valueMap = BitSet.valueOf(this._valueMap.toLongArray());
            _DNodeVerify.token = this.token;
            _DNodeVerify.idList = Collections.unmodifiableList(new ArrayList<>(this.idList));
            return _DNodeVerify;
        }

	    /** player token */
        public Builder setToken(String token) {
            this.token = token;
            this._valueMap.set(0);
            return this;
        }

        public Builder addAllIdList(List<Long> idList) {
            this.idList.addAll(idList);
            this._valueMap.set(1);
            return this;
        }

        public Builder addIdList(long idList) {
            this.idList.add(idList);
            this._valueMap.set(1);
            return this;
        }

    }
}
