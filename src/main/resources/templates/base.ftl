package ${packagePath};

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.gen.GameRPCGenFile;

import java.util.*;

<#if hasComment>
/**
<#list comments as comment>
 * ${comment}
</#list>
 */
</#if>
@GameRPCGenFile
public class ${structType} extends DSyncBase {
    public static final String TYPE_NAME = ${structType}.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(${structType}.class.getSimpleName());
    public static int FIELD_NUM = ${fields?size};

<#list fields as field>
<#if field.hasComment>
	/** ${field.comment} */
</#if>
	private ${field.typeName} ${field.name};

</#list>

    ${structType}() {
    }

    public static ${structType}.Builder newBuilder() {
        return new ${structType}.Builder();
    }

<#list fields as field>
    public ${field.typeName} get${field.largeName}() {
        return ${field.name};
    }

    public boolean has${field.largeName}() {
        return _changedMap.get(${field.index});
    }

</#list>
    @Override
    public void writeTo(OutputStream out) {
<#list fields as field>
        out.write(${field.name});
</#list>
    }

    @Override
    public void readFrom(InputStream in) {
<#list fields as field>
        ${field.name} = in.read();
</#list>
    }

    public static class Builder extends DSyncBase.Builder {
<#list fields as field>
<#if field.hasComment>
	    /** ${field.comment} */
</#if>
	    private ${field.typeName} ${field.name};

</#list>

        private Builder() {
            this._changedMap = new BitSet(FIELD_NUM);
        }

        public ${structType} build() {
            ${structType} ${structVarName} = new ${structType}();
            ${structVarName}._changedMap = new BitSet(FIELD_NUM);
            ${structVarName}._changedMap.or(this._changedMap);
<#list fields as field>
            ${structVarName}.${field.name} = this.${field.name};
</#list>
            return ${structVarName};
        }

<#list fields as field>
        public void set${field.largeName}(${field.typeName} ${field.name}) {
            this.${field.name} = ${field.name};
            this._changedMap.set(${field.index});
        }

</#list>
    }
}
