package pers.fj.concurrent.sample.threadpool;

/**
 * Created by fang_j on 2020/08/20.
 */
public class SetDefaultUncaughtExceptionHandler {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
           System.out.println(String.format("UncaughtException in Thread(%s): %s", t.getName(), e.getMessage()));
           e.printStackTrace();
        });

        new Thread(() -> Integer.parseInt("abc")).start();
    }
}
