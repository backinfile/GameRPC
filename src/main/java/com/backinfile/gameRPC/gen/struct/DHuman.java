package com.backinfile.gameRPC.gen.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;

import java.util.*;

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

    public int getPropsSize() {
        return props.size();
    }

    public List<DProp> getPropsList() {
        return props;
    }

    public boolean hasProps() {
        return _valueMap.get(0);
    }

    public DProp getSingleProp() {
        return singleProp;
    }

    public boolean hasSingleProp() {
        return _valueMap.get(1);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(props);
        out.write(singleProp);
    }

    @Override
    public void readFrom(InputStream in) {
        props = Collections.unmodifiableList(in.read());
        singleProp = in.read();
    }

    public static class Builder extends DSyncBase.Builder {
	    final private List<DProp> props = new ArrayList<>();
	    private DProp singleProp = null;

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public DHuman build() {
            DHuman _DHuman = new DHuman();
            _DHuman._valueMap = new BitSet(FIELD_NUM);
            _DHuman._valueMap.or(this._valueMap);
            _DHuman.props = List.copyOf(this.props);
            _DHuman.singleProp = this.singleProp;
            return _DHuman;
        }

        public void addAllProps(List<DProp> props) {
            this.props.addAll(props);
            this._valueMap.set(0);
        }

        public void addProps(DProp props) {
            this.props.add(props);
            this._valueMap.set(0);
        }

        public void setSingleProp(DProp singleProp) {
            this.singleProp = singleProp;
            this._valueMap.set(1);
        }

    }
}
