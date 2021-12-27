package ${packagePath};

<#if hasComment>
/**
<#list comments as comment>
 * ${comment}
</#list>
 */
</#if>
public enum ${className} {
<#list fields as field>
<#if field.hasComment>
    /** ${field.comment} */
</#if>
    ${field.name},
</#list>
}