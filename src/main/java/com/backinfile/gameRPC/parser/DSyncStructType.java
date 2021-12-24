package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DSyncStructType {
    Int("I", "Int", "int", "int32", "Integer"), // int

    Long("L", "Long", "long", "int64"), // long

    String("S", "String", "string", "str"), // string

    Double("D", "Double", "Float", "double", "float"), // float

    Boolean("B", "Boolean", "boolean", "bool"), // boolean

    Enum("E", "enum"), UserDefine("U");

    private final String shortName;
    private final List<String> names = new ArrayList<>();

    private DSyncStructType(String shortName, String... names) {
        this.shortName = shortName;
        this.names.addAll(Arrays.asList(names));
    }

    public static DSyncStructType match(String matchName) {
        for (var type : DSyncStructType.values()) {
            for (var typeName : type.names) {
                if (typeName.equals(matchName)) {
                    return type;
                }
            }
        }
        return UserDefine;
    }

    public String getShortName() {
        return shortName;
    }
}
