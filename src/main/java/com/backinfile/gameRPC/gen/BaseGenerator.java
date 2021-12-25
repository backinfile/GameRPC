package com.backinfile.gameRPC.gen;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.parser.DSyncStructType;
import com.backinfile.gameRPC.parser.SyntaxWorker;
import com.backinfile.gameRPC.parser.TokenWorker;
import com.backinfile.gameRPC.rpc.SysException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成框架内部需要的文件
 */
public class BaseGenerator {

    public static void main(String[] args) throws IOException {
        genStruct();
    }

    private static void genStruct() throws IOException {
        Log.gen.info("genStruct start");

        // 读取协议文件
        List<String> strings = FreeMarkerManager.readResource("base.gr");

        // 解析协议文件
        TokenWorker.Result tokens = TokenWorker.getTokens(strings);
        if (tokens.hasError) {
            throw new SysException(tokens.errorStr);
        }
        SyntaxWorker.Result result = SyntaxWorker.parse(tokens.tokens);
        if (result.hasError) {
            throw new SysException(result.errorStr);
        }

        // 生成
        for (var struct : result.userDefineStructMap.values()) {
            Map<String, Object> rootMap = new HashMap<>();
            List<Map<String, Object>> fields = new ArrayList<>();
            rootMap.put("fields", fields);
            rootMap.put("packagePath", result.properties.getOrDefault("java_package", "default"));
            rootMap.put("structType", struct.getTypeName());
            rootMap.put("structVarName", "_" + struct.getTypeName());
            rootMap.put("comments", struct.getComments());
            rootMap.put("hasComment", !struct.getComments().isEmpty());

            int i = 0;
            for (var field : struct.getChildren()) {
                final int index = i++;
                var fieldMap = new HashMap<String, Object>();
                fields.add(fieldMap);
                fieldMap.put("typeName", field.getTypeName());
                fieldMap.put("name", field.name);
                fieldMap.put("largeName", field.name.substring(0, 1).toUpperCase() + field.name.substring(1));
                fieldMap.put("array", field.isArray);
                fieldMap.put("baseType", field.type != DSyncStructType.UserDefine);
                fieldMap.put("enumType", false);
                fieldMap.put("equalType", field.isEqualType());
                fieldMap.put("copyType", field.type == DSyncStructType.UserDefine);
                fieldMap.put("largeTypeName", field.getLargeTypeName());
                fieldMap.put("singleTypeName", field.getSingleTypeName());
                fieldMap.put("longTypeName", field.getJSONLongTypeName());
                fieldMap.put("defaultValue", field.getDefaultValue());
                fieldMap.put("hasComment", !field.comment.isEmpty());
                fieldMap.put("comment", field.comment);
                fieldMap.put("index", index);

                if (field.type == DSyncStructType.UserDefine) {
                    var dSyncStruct = result.userDefineStructMap.get(field.typeName);
                    if (dSyncStruct.getType() == DSyncStructType.Enum) {
                        fieldMap.put("baseType", false);
                        fieldMap.put("enumType", true);
                        fieldMap.put("copyType", false);
                        if (!field.isArray) {
                            fieldMap.put("defaultValue", field.typeName + "." + dSyncStruct.getDefaultValue());
                        }
                    }
                }
            }
            FreeMarkerManager.formatFileInProj("templates", "base.ftl",
                    rootMap, "src/main/gen/com/backinfile/gameRPC/struct", struct.getTypeName() + ".java");
        }

    }
}