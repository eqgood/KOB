package org.kob.botrunningsystem.utils.sandbox;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotSecureClassLoader extends ClassLoader {
    private final Map<String, byte[]> secureBytecodes = new ConcurrentHashMap<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public BotSecureClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineSecureClasses(String mainClassName, Map<String, byte[]> bytecodes) {
        for (Map.Entry<String, byte[]> entry : bytecodes.entrySet()) {
            String className = entry.getKey();
            byte[] bytecode = entry.getValue();

            // 解析字节码结构，进行安全检查
            ClassReader cr = new ClassReader(bytecode);
            // 相当于带有原来类的结构和方法的 ClassWriter，后续将合法的字节码写入这个 ClassWriter
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            BotSecurityClassVisitor visitor = new BotSecurityClassVisitor(cw, className);

            try {
                // 这句才是真正的校验，读取到方法就调用 visitMethodInsn()，读到类就调用 visit()，然后将合法的字节码写入 ClassWriter
                cr.accept(visitor, ClassReader.EXPAND_FRAMES);
            } catch (SecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new SecurityException("沙箱拦截：字节码校验失败，存在恶意代码", e);
            }

            secureBytecodes.put(className, cw.toByteArray());
        }

        try {
            return loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Bot 主类加载失败: " + mainClassName, e);
        }
    }

    private Class<?> defineSecureClass(String className, byte[] bytecode) {
        // 解析字节码结构，进行安全检查
        return defineClass(className, bytecode, 0, bytecode.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytecode = secureBytecodes.get(name);
        if (bytecode != null) {
            return defineSecureClass(name, bytecode);
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            if (secureBytecodes.containsKey(name)) {
                Class<?> clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            return super.loadClass(name, resolve);
        }
    }
}