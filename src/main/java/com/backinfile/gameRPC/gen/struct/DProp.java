package com.backinfile.gameRPC.gen.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;

import java.util.*;

@GameRPCGenFile
public class DProp extends DSyncBase {
    public static final String TYPE_NAME = DProp.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DProp.class.getSimpleName());
    public static int FIELD_NUM = 2;

	private String propName;
	private double propValue;

    DProp() {
    }

    public static DProp.Builder newBuilder() {
        return new DProp.Builder();
    }

    public String getPropName() {
        return propName;
    }

    public boolean hasPropName() {
        return _valueMap.get(0);
    }

    public double getPropValue() {
        return propValue;
    }

    public boolean hasPropValue() {
        return _valueMap.get(1);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(_valueMap.toLongArray());
        out.write(propName);
        out.write(propValue);
    }

    @Override
    public void readFrom(InputStream in) {
        _valueMap = BitSet.valueOf((long[]) in.read());
        propName = in.read();
        propValue = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
	    private String propName = "";
	    private double propValue = 0f;

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public DProp build() {
            DProp _DProp = new DProp();
            _DProp._valueMap = BitSet.valueOf(this._valueMap.toLongArray());
            _DProp.propName = this.propName;
            _DProp.propValue = this.propValue;
            return _DProp;
        }

        public Builder setPropName(String propName) {
            this.propName = propName;
            this._valueMap.set(0);
            return this;
        }

        public Builder setPropValue(double propValue) {
            this.propValue = propValue;
            this._valueMap.set(1);
            return this;
        }

    }
}
