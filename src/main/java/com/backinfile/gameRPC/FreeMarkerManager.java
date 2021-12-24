package com.backinfile.gameRPC;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FreeMarkerManager {
    public static void formatFile(String filePath, String fileName, Map<String, Object> rootMap, String outPath,
                                  String outFileName) {
        try {
            var config = new Configuration(Configuration.VERSION_2_3_22);
            config.setDefaultEncoding("UTF-8");
            config.setDirectoryForTemplateLoading(new File(filePath));
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            var file = new File(outPath, outFileName);
            file.getParentFile().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                var template = config.getTemplate(fileName, "UTF-8");
                template.process(rootMap, writer);
            }
            Log.gen.info("gen {} success", file.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void formatFile(String fileName, Map<String, Object> rootMap, String outPath, String outFileName) {
        try {
            var config = new Configuration(Configuration.VERSION_2_3_22);
            config.setDefaultEncoding("UTF-8");
            config.setClassForTemplateLoading(FreeMarkerManager.class, "");
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            var file = new File(outPath, outFileName);
            file.getParentFile().mkdirs();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                var template = config.getTemplate(fileName, "UTF-8");
                template.process(rootMap, writer);
            }
            Log.gen.info("gen {} success", file.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
