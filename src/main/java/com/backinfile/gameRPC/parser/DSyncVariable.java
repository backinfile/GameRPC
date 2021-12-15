package com.backinfile.gameRPC.parser;

public class DSyncVariable {
    public String name;
    public DSyncStructType type;
    public String typeName;
    public boolean isArray = false;
    public String comment = "";

    public DSyncVariable() {
    }

    public DSyncVariable(String name, DSyncStructType type, boolean isArray) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        if (isArray) {
            return "List<" + getLargeTypeName() + ">";
        }
        String typeName = "";
        switch (type) {
            case Boolean:
                typeName = "boolean";
                break;
            case Double:
                typeName = "double";
                break;
            case Int:
                typeName = "int";
                break;
            case Long:
                typeName = "long";
                break;
            case String:
                typeName = "String";
                break;
            case Enum:
            case UserDefine:
                typeName = this.typeName;
                break;
            default:
                break;
        }
        return typeName;
    }

    public String getSingleTypeName() {
        String typeName = "";
        switch (type) {
            case Boolean:
                typeName = "boolean";
                break;
            case Double:
                typeName = "double";
                break;
            case Int:
                typeName = "int";
                break;
            case Long:
                typeName = "long";
                break;
            case String:
                typeName = "String";
                break;
            case Enum:
            case UserDefine:
                typeName = this.typeName;
                break;
            default:
                break;
        }
        return typeName;
    }

    public String getLargeTypeName() {
        String typeName = "";
        switch (type) {
            case Boolean:
                typeName = "Boolean";
                break;
            case Double:
                typeName = "Double";
                break;
            case Int:
                typeName = "Integer";
                break;
            case Long:
                typeName = "Long";
                break;
            case String:
                typeName = "String";
                break;
            case Enum:
            case UserDefine:
                typeName = this.typeName;
                break;
            default:
                break;
        }
        return typeName;
    }

    public String getJSONLongTypeName() {
        String typeName = "";
        switch (type) {
            case Boolean:
                typeName = "BooleanValue";
                break;
            case Int:
                typeName = "IntValue";
                break;
            case Long:
                typeName = "LongValue";
                break;
            case Double:
                typeName = "DoubleValue";
                break;
            case String:
                typeName = "String";
                break;
            case Enum:
            case UserDefine:
                typeName = this.typeName;
                break;
            default:
                break;
        }
        return typeName;
    }

    public String getDefaultValue() {
        if (isArray) {
            return "new ArrayList<>()";
        }
        switch (type) {
            case Boolean:
                return "false";
            case Int:
                return "0";
            case Long:
                return "0";
            case Double:
                return "0f";
            case String:
                return "\"\"";
            case Enum:
                return "null"; // 临时使用
            case UserDefine:
                return "null";
            default:
                break;
        }
        return "null";
    }

    public boolean isEqualType() {
        if (isArray) {
            return true;
        }
        return type == DSyncStructType.UserDefine || type == DSyncStructType.Enum || type == DSyncStructType.String;
    }

}
