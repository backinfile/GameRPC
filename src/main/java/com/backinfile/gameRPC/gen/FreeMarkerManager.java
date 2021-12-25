package com.backinfile.gameRPC.gen;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.SysException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FreeMarkerManager {
    /**
     * 读取工程外的模板文件生成文件
     */
    public static void formatFile(String filePath, String fileName, Map<String, Object> rootMap, String outPath,
                                  String outFileName) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_22);
            config.setDefaultEncoding("UTF-8");
            config.setDirectoryForTemplateLoading(new File(filePath));
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            formatTemplate(config, fileName, rootMap, outPath, outFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取工程内的模板文件生成文件
     */
    public static void formatFileInProj(String templatePath, String fileName,
                                        Map<String, Object> rootMap, String outPath, String outFileName) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_22);
            config.setDefaultEncoding("UTF-8");
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            config.setClassLoaderForTemplateLoading(FreeMarkerManager.class.getClassLoader(), templatePath);

            formatTemplate(config, fileName, rootMap, outPath, outFileName);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    private static void formatTemplate(Configuration config, String fileName, Map<String, Object> rootMap,
                                       String outPath, String outFileName) throws Exception {
        File file = new File(outPath, outFileName);
        Log.gen.info("start gen {}", file.getPath());
        file.getParentFile().mkdirs();
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            Template template = config.getTemplate(fileName, "UTF-8");
            template.process(rootMap, writer);
        }
        Log.gen.info("gen {} success\n", file.getPath());
    }


    /**
     * 读取资源文件
     */
    public static List<String> readResource(String resourceFile) {
        List<String> result = new ArrayList<>();
        InputStream in = FreeMarkerManager.class.getClassLoader().getResourceAsStream(resourceFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        while (true) {
            String line = null;
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
            result.add(line);
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
