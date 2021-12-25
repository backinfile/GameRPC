package default;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.BitSet;
import java.util.Objects;

public class DNodeVerify extends DSyncBase {
    public static final String TYPE_NAME = DNodeVerify.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DNodeVerify.class.getSimpleName());
    public static int FIELD_NUM = 0;

    private long id;
    private String name;

    DNodeVerify() {
    }

    public static DNodeVerify.Builder newBuilder() {
        return new DNodeVerify.Builder();
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
            this._changedMap = new BitSet(FIELD_NUM);
        }

        public DHuman build() {
            DNodeVerify _DNodeVerify = new DNodeVerify();
            _DNodeVerify._changedMap = new BitSet(FIELD_NUM);
            _DNodeVerify._changedMap.or(this._changedMap);
            _DNodeVerify.id = this.id;
            _DNodeVerify.name = this.name;
            return _DNodeVerify;
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
