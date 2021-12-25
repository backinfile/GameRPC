package com.backinfile.gameRPC.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import java.util.BitSet;
import java.util.Objects;

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
        return _changedMap.get(0);
    }

    public double getPropValue() {
        return propValue;
    }

    public boolean hasPropValue() {
        return _changedMap.get(1);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(propName);
        out.write(propValue);
    }

    @Override
    public void readFrom(InputStream in) {
        propName = in.read();
        propValue = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
	    private String propName;

	    private double propValue;


        private Builder() {
            this._changedMap = new BitSet(FIELD_NUM);
        }

        public DProp build() {
            DProp _DProp = new DProp();
            _DProp._changedMap = new BitSet(FIELD_NUM);
            _DProp._changedMap.or(this._changedMap);
            _DProp.propName = this.propName;
            _DProp.propValue = this.propValue;
            return _DProp;
        }

        public void setPropName(String propName) {
            this.propName = propName;
            this._changedMap.set(0);
        }

        public void setPropValue(double propValue) {
            this.propValue = propValue;
            this._changedMap.set(1);
        }

    }
}
