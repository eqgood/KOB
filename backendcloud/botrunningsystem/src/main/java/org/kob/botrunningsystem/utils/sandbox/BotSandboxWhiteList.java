package org.kob.botrunningsystem.utils.sandbox;

import java.util.*;

public class BotSandboxWhiteList {
    public static final Set<String> ALLOWED_CLASSES = new HashSet<>();
    public static final Set<String> ALLOWED_PACKAGES = new HashSet<>();

    static {
        // 基础核心类
        ALLOWED_CLASSES.add(Object.class.getName());
        ALLOWED_CLASSES.add(String.class.getName());
        ALLOWED_CLASSES.add(Integer.class.getName());
        ALLOWED_CLASSES.add(Long.class.getName());
        ALLOWED_CLASSES.add(Math.class.getName());
        ALLOWED_CLASSES.add(Throwable.class.getName());
        ALLOWED_CLASSES.add(Exception.class.getName());
        ALLOWED_CLASSES.add(RuntimeException.class.getName());

        // 必要的 IO 类（仅用于读 input.txt）
        ALLOWED_CLASSES.add(java.io.File.class.getName());
        ALLOWED_CLASSES.add(java.util.Scanner.class.getName());
        ALLOWED_CLASSES.add(java.io.FileInputStream.class.getName());
        ALLOWED_CLASSES.add(java.io.FileNotFoundException.class.getName());

        // Bot 模板中的内部类
        ALLOWED_CLASSES.add("org.kob.botrunningsystem.utils.Bot$Cell");

        // 函数式接口和集合
        ALLOWED_PACKAGES.add("java.util.function");

        // 改为添加具体需要的类
        ALLOWED_CLASSES.add(ArrayList.class.getName());
        ALLOWED_CLASSES.add(List.class.getName());
        ALLOWED_CLASSES.add(HashMap.class.getName());
        ALLOWED_CLASSES.add(Map.class.getName());
        ALLOWED_CLASSES.add(Set.class.getName());
        ALLOWED_CLASSES.add(HashSet.class.getName());
        ALLOWED_CLASSES.add(Queue.class.getName());
        ALLOWED_CLASSES.add(LinkedList.class.getName());

        ALLOWED_PACKAGES.add("java.util.stream");
        ALLOWED_CLASSES.add(java.util.Iterator.class.getName());
        ALLOWED_CLASSES.add(java.util.ListIterator.class.getName());
    }

    public static boolean isAllowed(String className) {
        if (ALLOWED_CLASSES.contains(className)) {
            return true;
        }
        for (String pkg : ALLOWED_PACKAGES) {
            if (className.startsWith(pkg + ".")) {
                return true;
            }
        }
        return false;
    }
}