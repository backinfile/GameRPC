package com.backinfile.gameRPC.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import java.util.BitSet;
import java.util.Objects;

@GameRPCGenFile
public class DHuman extends DSyncBase {
    public static final String TYPE_NAME = DHuman.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DHuman.class.getSimpleName());
    public static int FIELD_NUM = 2;

	private List<DProp> props;

	private DProp singleProp;


    DHuman() {
    }

    public static DHuman.Builder newBuilder() {
        return new DHuman.Builder();
    }

    public List<DProp> getProps() {
        return props;
    }

    public boolean hasProps() {
        return _changedMap.get(0);
    }

    public DProp getSingleProp() {
        return singleProp;
    }

    public boolean hasSingleProp() {
        return _changedMap.get(1);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(props);
        out.write(singleProp);
    }

    @Override
    public void readFrom(InputStream in) {
        props = in.read();
        singleProp = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
	    private List<DProp> props;

	    private DProp singleProp;


        private Builder() {
            this._changedMap = new BitSet(FIELD_NUM);
        }

        public DHuman build() {
            DHuman _DHuman = new DHuman();
            _DHuman._changedMap = new BitSet(FIELD_NUM);
            _DHuman._changedMap.or(this._changedMap);
            _DHuman.props = this.props;
            _DHuman.singleProp = this.singleProp;
            return _DHuman;
        }

        public void setProps(List<DProp> props) {
            this.props = props;
            this._changedMap.set(0);
        }

        public void setSingleProp(DProp singleProp) {
            this.singleProp = singleProp;
            this._changedMap.set(1);
        }

    }
}
