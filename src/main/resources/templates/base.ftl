package ${packagePath};

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.BitSet;
import java.util.Objects;

public class ${structType} extends DSyncBase {
    public static final String TYPE_NAME = ${structType}.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(${structType}.class.getSimpleName());
    public static int FIELD_NUM = ${fields?size};

    private long id;
    private String name;

    ${structType}() {
    }

    public static ${structType}.Builder newBuilder() {
        return new ${structType}.Builder();
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
            ${structType} ${structVarName} = new ${structType}();
            ${structVarName}._changedMap = new BitSet(FIELD_NUM);
            ${structVarName}._changedMap.or(this._changedMap);
            ${structVarName}.id = this.id;
            ${structVarName}.name = this.name;
            return ${structVarName};
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
