package com.backinfile.gameRPC.parser;

import com.backinfile.gameRPC.FreeMarkerManager;
import com.backinfile.gameRPC.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DSyncGenerator {
    private String templatePath;
    private String templateName;
    private String outPackagePath = "com.backinfile.dSync.demo";
    private String outFilePath = "src\\main\\java\\com\\backinfile\\dSync\\demo";
    private String outClassName = "HandlerDemo";
    private String dsSourceFilePath = "";
    private String dsSource = "";

    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        Log.gen.info("gen");

        if (args.length != 6) {
            System.out.println("usage: java -jar dSync.jar outPackagePath outFilePath outClassName dsPath templatePath templateName");
            return;
        }
        var generator = new DSyncGenerator();
        generator.outPackagePath = args[0];
        generator.outFilePath = args[1];
        generator.outClassName = args[2];
        generator.dsSourceFilePath = args[3];
        generator.templatePath = args[4];
        generator.templateName = args[5];
        generator.genFile();
    }

    public void genFile() {
        dsSource = getDSSource();
        doGenFile();
    }

    private String getDSSource() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(dsSourceFilePath));
            var sj = new StringJoiner("\n");
            for (var line : lines) {
                sj.add(line);
            }
            return sj.toString();
        } catch (IOException e) {
            throw new DSyncException(e);
        }
    }

    private SyntaxWorker.Result getResult() {
        var tokenResult = TokenWorker.getTokens(dsSource);
        return SyntaxWorker.parse(tokenResult.tokens);
    }

    private void doGenFile() {
        var result = getResult();
        var rootMap = new HashMap<String, Object>();
        var structs = new ArrayList<Map<String, Object>>();
        var enums = new ArrayList<Map<String, Object>>();

        if (result.hasError) {
            throw new DSyncException(result.errorStr);
        }

        rootMap.put("packagePath", outPackagePath);
        rootMap.put("handlerClassName", outClassName);
        rootMap.put("structs", structs);
        rootMap.put("enums", enums);
        rootMap.put("hasMsg", !result.messageStructs.isEmpty());
        for (var struct : result.userDefineStructMap.values()) {
            if (struct.getType() == DSyncStructType.Enum) {
                continue;
            }
            var structMap = new HashMap<String, Object>();
            structs.add(structMap);
            var fields = new ArrayList<Map<String, Object>>();
            structMap.put("className", struct.getTypeName());
            structMap.put("fields", fields);
            structMap.put("comments", struct.getComments());
            structMap.put("hasComment", !struct.getComments().isEmpty());
            structMap.put("isMsg", result.messageStructs.contains(struct.getTypeName()));
            for (var field : struct.getChildren()) {
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
        }

        for (var struct : result.userDefineStructMap.values()) {
            if (struct.getType() != DSyncStructType.Enum) {
                continue;
            }
            var enumMap = new HashMap<String, Object>();
            enums.add(enumMap);
            var fields = new ArrayList<Map<String, Object>>();
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
        }

        FreeMarkerManager.formatFile(templatePath, templateName, rootMap, outFilePath, outClassName + ".java");
    }

}
