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
<#if field.array>
    public int get${field.largeName}Size() {
        return ${field.name}.size();
    }

    public ${field.typeName} get${field.largeName}List() {
        return ${field.name};
    }

    public boolean has${field.largeName}() {
        return _valueMap.get(${field.index});
    }
<#else>
    public ${field.typeName} get${field.largeName}() {
        return ${field.name};
    }

    public boolean has${field.largeName}() {
        return _valueMap.get(${field.index});
    }
</#if>

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
<#if field.array>
        ${field.name} = Collections.unmodifiableList(in.read());
<#else>
        ${field.name} = in.read();
</#if>
</#list>
    }

    public static class Builder extends DSyncBase.Builder {
<#list fields as field>
<#if field.hasComment>
	    /** ${field.comment} */
</#if>
<#if field.array>
	    final private ${field.typeName} ${field.name} = ${field.defaultValue};
<#else>
	    private ${field.typeName} ${field.name} = ${field.defaultValue};
</#if>
</#list>

        private Builder() {
            this._valueMap = new BitSet(FIELD_NUM);
        }

        public ${structType} build() {
            ${structType} ${structVarName} = new ${structType}();
            ${structVarName}._valueMap = new BitSet(FIELD_NUM);
            ${structVarName}._valueMap.or(this._valueMap);
<#list fields as field>
<#if field.array>
            ${structVarName}.${field.name} = List.copyOf(this.${field.name});
<#else>
            ${structVarName}.${field.name} = this.${field.name};
</#if>
</#list>
            return ${structVarName};
        }

<#list fields as field>
<#if field.array>
        public void addAll${field.largeName}(${field.typeName} ${field.name}) {
            this.${field.name}.addAll(${field.name});
            this._valueMap.set(${field.index});
        }

        public void add${field.largeName}(${field.singleTypeName} ${field.name}) {
            this.${field.name}.add(${field.name});
            this._valueMap.set(${field.index});
        }
<#else>
        public void set${field.largeName}(${field.typeName} ${field.name}) {
            this.${field.name} = ${field.name};
            this._valueMap.set(${field.index});
        }
</#if>

</#list>
    }
}
