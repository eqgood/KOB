package org.kob.botrunningsystem.utils.sandbox;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BotSecurityClassVisitor extends ClassVisitor {
    private final String className;
    private static final int ASM_API = Opcodes.ASM9;

    public BotSecurityClassVisitor(ClassVisitor classVisitor, String className) {
        super(ASM_API, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new BotSecurityMethodVisitor(ASM_API, mv, className);
    }
    
    private static class BotSecurityMethodVisitor extends MethodVisitor {
        private final String className;
    
        // api ASM 版本号
        public BotSecurityMethodVisitor(int api, MethodVisitor mv, String className) {
            super(api, mv);
            this.className = className;
        }
    
        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String targetClassName = Type.getObjectType(owner).getClassName();
        
            // 1. 检查是否是 Bot 类自身或其内部类（如 Bot$Cell）
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            String expectedCellName = className + "$Cell";
            String simpleCellName = simpleClassName + "$Cell";

            if (targetClassName.equals(expectedCellName) ||
                    targetClassName.equals(simpleCellName) ||
                    targetClassName.equals(className) ||
                    targetClassName.equals(simpleClassName)) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                return;
            }

            // 2. 检查类是否在白名单内
            if (!BotSandboxWhiteList.isAllowed(targetClassName)) {
                throw new SecurityException("沙箱拦截：禁止调用危险类 " + targetClassName + " 的方法 " + name);
            }

            // 2. 拦截高危操作
            if (targetClassName.equals(System.class.getName()) && name.equals("exit")) {
                throw new SecurityException("沙箱拦截：禁止调用 System.exit()");
            }
            if (targetClassName.equals(Runtime.class.getName()) && name.equals("exec")) {
                throw new SecurityException("沙箱拦截：禁止执行系统命令");
            }

            // 3. 细粒度拦截 File 的危险方法（只允许读，禁止写/删/重命名）
            if (targetClassName.equals(java.io.File.class.getName())) {
                if (name.equals("delete") || name.equals("deleteOnExit")
                        || name.equals("createNewFile") || name.equals("mkdir") || name.equals("mkdirs")
                        || name.equals("renameTo") || name.equals("setWritable") || name.equals("setReadOnly")) {
                    throw new SecurityException("沙箱拦截：禁止文件写/删除/重命名操作");
                }
            }
            // 将合法的调用继续传递给下一个 MethodVisitor，也就是 cw 的methodVisitor，最终写入 ClassWriter
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}