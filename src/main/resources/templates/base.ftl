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
<#if field.hasComment>
	/** ${field.comment} */
</#if>
    public int get${field.largeName}Size() {
        return ${field.name}.size();
    }

<#if field.hasComment>
	/** ${field.comment} */
</#if>
    public ${field.typeName} get${field.largeName}List() {
        return ${field.name};
    }

<#if field.hasComment>
	/** ${field.comment} */
</#if>
    public boolean has${field.largeName}() {
        return _valueMap.get(${field.index});
    }
<#else>
<#if field.hasComment>
	/** ${field.comment} */
</#if>
    public ${field.typeName} get${field.largeName}() {
        return ${field.name};
    }

<#if field.hasComment>
	/** ${field.comment} */
</#if>
    public boolean has${field.largeName}() {
        return _valueMap.get(${field.index});
    }
</#if>

</#list>
    @Override
    public void writeTo(OutputStream out) {
        out.write(_valueMap.toLongArray());
<#list fields as field>
        out.write(${field.name});
</#list>
    }

    @Override
    public void readFrom(InputStream in) {
        _valueMap = BitSet.valueOf((long[]) in.read());
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
            ${structVarName}._valueMap = BitSet.valueOf(this._valueMap.toLongArray());
<#list fields as field>
<#if field.array>
            ${structVarName}.${field.name} = Collections.unmodifiableList(new ArrayList<>(this.${field.name}));
<#else>
            ${structVarName}.${field.name} = this.${field.name};
</#if>
</#list>
            return ${structVarName};
        }

<#list fields as field>
<#if field.array>
<#if field.hasComment>
	    /** ${field.comment} */
</#if>
        public Builder addAll${field.largeName}(${field.typeName} ${field.name}) {
            this.${field.name}.addAll(${field.name});
            this._valueMap.set(${field.index});
            return this;
        }

<#if field.hasComment>
	    /** ${field.comment} */
</#if>
        public Builder add${field.largeName}(${field.singleTypeName} ${field.name}) {
            this.${field.name}.add(${field.name});
            this._valueMap.set(${field.index});
            return this;
        }
<#else>
<#if field.hasComment>
	    /** ${field.comment} */
</#if>
        public Builder set${field.largeName}(${field.typeName} ${field.name}) {
            this.${field.name} = ${field.name};
            this._valueMap.set(${field.index});
            return this;
        }
</#if>

</#list>
    }
}
