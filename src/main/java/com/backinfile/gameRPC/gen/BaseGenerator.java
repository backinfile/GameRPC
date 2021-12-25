package com.backinfile.gameRPC.gen;

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
            rootMap.put("packagePath", result.properties.getOrDefault("java_package", "default"));
            rootMap.put("structType", struct.getTypeName());
            rootMap.put("structVarName", "_" + struct.getTypeName());
            rootMap.put("fields", new ArrayList<String>());

            FreeMarkerManager.formatFileInProj("templates", "base.ftl",
                    rootMap, "src/main/gen/com/backinfile/gameRPC/struct", "DBase.java");
        }

    }
}