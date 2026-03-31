package org.kob.botrunningsystem.utils.sandbox;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class BotSecureSandbox {
    private static final ExecutorService SANDBOX_EXECUTOR = new ThreadPoolExecutor(
            2,
            4,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("bot-sandbox-thread");
                return t;
            }
    );

    private static final long EXECUTE_TIMEOUT_MS = 1000;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SANDBOX_EXECUTOR.shutdown();
            try {
                if (!SANDBOX_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    SANDBOX_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                SANDBOX_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }

    @SuppressWarnings("unchecked")
    public static Integer executeSafe(String botCode, String className) {
        try {
            Map<String, byte[]> bytecodes = JdkCompiler.compile(className, botCode);
    
            BotSecureClassLoader classLoader = new BotSecureClassLoader(Thread.currentThread().getContextClassLoader());
            Class<?> botClass = classLoader.defineSecureClasses(className, bytecodes);
    
            Future<Integer> future = SANDBOX_EXECUTOR.submit(() -> {
                Supplier<Integer> botInstance =
                        (Supplier<Integer>) botClass.getDeclaredConstructor().newInstance();
                return botInstance.get();
            });
    
            return future.get(EXECUTE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    
        } catch (TimeoutException e) {
            throw new SecurityException("沙箱拦截：Bot 代码执行超时，存在死循环");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SecurityException) {
                throw (SecurityException) cause;
            }
            throw new RuntimeException("Bot 代码执行异常", cause);
        } catch (Exception e) {
            throw new RuntimeException("Bot 代码加载失败", e);
        }
    }
}