package ${packagePath};
import com.backinfile.gameRPC.gen.GameRPCGenFile;

<#if hasComment>
/**
<#list comments as comment>
 * ${comment}
</#list>
 */
</#if>
@GameRPCGenFile
public enum ${className} {
<#list fields as field>
<#if field.hasComment>
    /** ${field.comment} */
</#if>
    ${field.name},
</#list>
}