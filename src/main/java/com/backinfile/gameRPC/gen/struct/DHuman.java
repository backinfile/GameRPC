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
    public static int FIELD_NUM = 3;

	private long id;
	private String name;
	private List<DProp> props;

    DHuman() {
    }

    public static DHuman.Builder newBuilder() {
        return new DHuman.Builder();
    }

    public long getId() {
        return id;
    }

    public boolean hasId() {
        return _valueMap.get(0);
    }

    public String getName() {
        return name;
    }

    public boolean hasName() {
        return _valueMap.get(1);
    }

    public int getPropsSize() {
        return props.size();
    }

    public List<DProp> getPropsList() {
        return props;
    }

    public boolean hasProps() {
        return _valueMap.get(2);
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(id);
        out.write(name);
        out.write(props);
    }

    @Override
    public void readFrom(InputStream in) {
        id = in.read();
        name = in.read();
        props = Collections.unmodifiableList(in.read());
    }

    public static class Builder extends DSyncBase.Builder {
	    private long id = 0;
	    private String name = "";
	    final private List<DProp> props = new ArrayList<>();

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public DHuman build() {
            DHuman _DHuman = new DHuman();
            _DHuman._valueMap = new BitSet(FIELD_NUM);
            _DHuman._valueMap.or(this._valueMap);
            _DHuman.id = this.id;
            _DHuman.name = this.name;
            _DHuman.props = List.copyOf(this.props);
            return _DHuman;
        }

        public void setId(long id) {
            this.id = id;
            this._valueMap.set(0);
        }

        public void setName(String name) {
            this.name = name;
            this._valueMap.set(1);
        }

        public void addAllProps(List<DProp> props) {
            this.props.addAll(props);
            this._valueMap.set(2);
        }

        public void addProps(DProp props) {
            this.props.add(props);
            this._valueMap.set(2);
        }

    }
}
