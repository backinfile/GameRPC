package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DSyncStructType {
    Int("Int", "int", "int32", "Integer"), // int
    Long("Long", "long", "int64"), // long
    String("String", "string", "str"), // string
    Double("Double", "Float", "double", "float"), // float
    Boolean("Boolean", "boolean", "bool"), // boolean
    Enum("enum"), UserDefine;

    private final List<String> names = new ArrayList<>();

    private DSyncStructType(String... names) {
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
}
