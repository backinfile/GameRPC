package com.backinfile.gameRPC.gen;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.parser.DSyncStructType;
import com.backinfile.gameRPC.parser.DSyncVariable;
import com.backinfile.gameRPC.parser.SyntaxWorker;
import com.backinfile.gameRPC.parser.TokenWorker;
import com.backinfile.gameRPC.rpc.SysException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 生成框架内部需要的文件
 */
public class BaseGenerator {

    public static final String GEN_PATH = "src/main/java/com/backinfile/gameRPC/gen/";
    public static final String GEN_STRUCT_PATH = GEN_PATH + "struct";
    public static final String GEN_SERVICE_PATH = GEN_PATH + "service";

    public static void main(String[] args) throws IOException {
        Log.gen.info("genStruct start");
        var result = prepareProto();
        clearGenFiles();
        genStruct(result);
        genEnum(result);
        genService(result);
    }


    // 清理旧文件
    private static void clearGenFiles() {
        {
            Log.gen.info("清理 struct\n");
            var folder = new File(GEN_STRUCT_PATH);
            for (String path : folder.list()) {
                if (!new File(folder, path).delete()) {
                    Log.gen.warn("清理{}失败", path);
                }
            }
        }
        {
            Log.gen.info("清理 service\n");
            var folder = new File(GEN_SERVICE_PATH);
            for (String path : folder.list()) {
                if (!new File(folder, path).delete()) {
                    Log.gen.warn("清理{}失败", path);
                }
            }
        }
    }

    // 读取协议文件
    private static SyntaxWorker.Result prepareProto() {
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
        return result;
    }

    // 生成自定义类
    private static void genStruct(SyntaxWorker.Result result) throws IOException {
        String packageName = result.properties.getOrDefault("java_package", "default");
        String structPackage = packageName + ".struct";
        String servicePackage = packageName + ".service";

        for (var struct : result.userDefineStructMap.values()) {
            if (struct.getType() == DSyncStructType.Enum) {
                continue;
            }
            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("fields", getParamRootMap(result, struct.getChildren()));
            rootMap.put("packagePath", structPackage);
            rootMap.put("structType", struct.getTypeName());
            rootMap.put("structVarName", "_" + struct.getTypeName());
            rootMap.put("comments", struct.getComments());
            rootMap.put("hasComment", !struct.getComments().isEmpty());

            FreeMarkerManager.formatFileInProj("templates", "base.ftl",
                    rootMap, GEN_STRUCT_PATH, struct.getTypeName() + ".java");
        }

    }

    private static void genService(SyntaxWorker.Result result) {
        String packageName = result.properties.getOrDefault("java_package", "default");
        String structPackage = packageName + ".struct";
        String servicePackage = packageName + ".service";

        for (var service : result.serviceMap.values()) {
            String serviceType = "Abstract" + service.name + "Service";
            var rootMap = new HashMap<String, Object>();
            rootMap.put("packagePath", servicePackage);
            rootMap.put("serviceName", service.name);
            rootMap.put("serviceType", serviceType);
            rootMap.put("imports", Collections.singletonList(structPackage + ".*"));
            rootMap.put("comments", service.comments);
            rootMap.put("hasComment", !service.comments.isEmpty());

            List<Map<String, Object>> rpcList = new ArrayList<>();
            rootMap.put("rpcList", rpcList);
            for (var rpc : service.rpcList) {
                Map<String, Object> rpcRootMap = new HashMap<>();
                rpcList.add(rpcRootMap);
                rpcRootMap.put("name", rpc.name);
                rpcRootMap.put("callParams", getParamRootMap(result, rpc.callParams));
                rpcRootMap.put("returnParams", getParamRootMap(result, rpc.returnParams));
                rpcRootMap.put("hashCode", rpc.getMethodHashCode());
                rpcRootMap.put("hashName", rpc.getMethodHashName());
                rpcRootMap.put("callString", rpc.getMethodCallString());
                if (rpc.clientVar != null) {
                    rpcRootMap.put("clientVar", getParamRootMap(result, Collections.singletonList(rpc.clientVar)).get(0));
                }
            }

            FreeMarkerManager.formatFileInProj("templates", "service.ftl",
                    rootMap, GEN_SERVICE_PATH, serviceType + ".java");
        }
    }

    private static List<Map<String, Object>> getParamRootMap(SyntaxWorker.Result result, List<DSyncVariable> variables) {
        List<Map<String, Object>> rootMapList = new ArrayList<>();
        int i = 0;

        for (var field : variables) {
            final int index = i++;
            Map<String, Object> fieldMap = new HashMap<>();
            rootMapList.add(fieldMap);
            // 变量的类型
            fieldMap.put("typeName", field.getTypeName());
            // 变量名
            fieldMap.put("name", field.name);
            // 变量名首字母大写
            fieldMap.put("largeName", field.name.substring(0, 1).toUpperCase() + field.name.substring(1));
            fieldMap.put("array", field.isArray);
            fieldMap.put("baseType", field.type != DSyncStructType.UserDefine);
            fieldMap.put("enumType", false);
            fieldMap.put("equalType", field.isEqualType());
            fieldMap.put("copyType", field.type == DSyncStructType.UserDefine);
            // 去掉list,转化为包装类
            fieldMap.put("largeTypeName", field.getLargeTypeName());
            // 去掉list
            fieldMap.put("singleTypeName", field.getSingleTypeName());
            fieldMap.put("longTypeName", field.getJSONLongTypeName());
            // 默认值
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
        return rootMapList;
    }

    private static void genEnum(SyntaxWorker.Result result) {
        String packageName = result.properties.getOrDefault("java_package", "default");
        String structPackage = packageName + ".struct";
        String servicePackage = packageName + ".service";

        // 生成自定义枚举
        for (var struct : result.userDefineStructMap.values()) {
            if (struct.getType() != DSyncStructType.Enum) {
                continue;
            }
            var enumMap = new HashMap<String, Object>();
            var fields = new ArrayList<Map<String, Object>>();
            enumMap.put("packagePath", structPackage);
            enumMap.put("className", struct.getTypeName());
            enumMap.put("fields", fields);
            enumMap.put("comments", struct.getComments());
            enumMap.put("hasComment", !struct.getComments().isEmpty());
            enumMap.put("defaultValue", struct.getDefaultValue());
            for (var field : struct.getChildren()) {
                var fieldMap = new HashMap<String, Object>();
                fields.add(fieldMap);
                fieldMap.put("name", field.name);
                fieldMap.put("hasComment", !field.comment.isEmpty());
                fieldMap.put("comment", field.comment);
            }
            FreeMarkerManager.formatFileInProj("templates", "enum.ftl",
                    enumMap, GEN_STRUCT_PATH, struct.getTypeName() + ".java");
        }
    }
}