package com.backinfile.support.reflection;

import com.backinfile.support.Utils;
import com.backinfile.support.log.UtilLog;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.text.MessageFormat;

public class Reflections {

    public static String getAllClassName(String packageName) {
        StringBuilder sb = new StringBuilder();
        for (String name : ReflectionUtils.getClassNames(packageName)) {
            sb.append(name).append('\n');
        }
        return sb.toString();
    }

    // 这个函数需要需改class文件，所以执行这个函数前尽量少加载类
    public static void classRewriteInit(String packageName, ClassLoader... classLoaders) {
        ClassPool pool = ClassPool.getDefault();
        for (ClassLoader classLoader : classLoaders) {
            pool.appendClassPath(new LoaderClassPath(classLoader));
        }


        for (String targetClassName : ReflectionUtils.getClassNames(packageName)) {
            try {
                boolean needRewrite = false;
                CtClass ctClass = pool.get(targetClassName);
                needRewrite = timingRewrite(pool, ctClass);
                needRewrite |= invokeLogRewrite(pool, ctClass);
                if (needRewrite) {
                    ctClass.toClass();
                    UtilLog.reflection.info("rewrite class {}", targetClassName);
                }
            } catch (Exception e) {
                UtilLog.reflection.error(e, "error in rewrite class {}", targetClassName);
            }
        }
    }

    public static boolean timingRewrite(ClassPool pool, CtClass ctClass) throws Exception {
        CtClass timeLoggerCtClass = pool.get(TimeLogger.class.getName());
        boolean needRewrite = false;
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            Timing timing = (Timing) ctMethod.getAnnotation(Timing.class);
            if (timing == null) {
                continue;
            }
            String loggerName = Utils.isNullOrEmpty(timing.value()) ? ctMethod.getName() : timing.value();
            ctMethod.addLocalVariable("$timeLogger", timeLoggerCtClass);
            ctMethod.insertBefore("$timeLogger = new " + TimeLogger.class.getName() + "(\"" + loggerName + "\");");
            ctMethod.insertAfter("$timeLogger.log();");
            needRewrite = true;
        }
        return needRewrite;
    }

    public static boolean invokeLogRewrite(ClassPool pool, CtClass ctClass) throws Exception {
        boolean needRewrite = false;
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            LogInvokeInfo annotation = (LogInvokeInfo) ctMethod.getAnnotation(LogInvokeInfo.class);
            if (annotation == null) {
                continue;
            }
            String pattern;
            if (!annotation.value().isEmpty()) {
                pattern = annotation.value();
            } else if (annotation.args()) {
                pattern = LogInvokeInfo.DEFAULT_PATTERN_ARGS;
            } else {
                pattern = LogInvokeInfo.DEFAULT_PATTERN;
            }
            String codePattern = MessageFormat.format("{0}.invoke.info({1});", UtilLog.class.getName(), pattern);
            String code = MessageFormat.format(codePattern, ctMethod.getDeclaringClass().getSimpleName(),
                    ctMethod.getName(), "\"args:\"+java.util.Arrays.toString($args)");
            ctMethod.insertBefore(code);
            needRewrite = true;
        }
        return needRewrite;
    }
}
