package pers.fj.concurrent.sample.collection;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fang_j on 2021/01/08.
 */
public class BadPractice {
    // 1. 使用并发对象 AtomicReference 包含了非并发的容器
    public static void main(String[] args) throws InterruptedException {
        AtomicReference<List<Long>> nums = new AtomicReference<>(Lists.newArrayList());
        CountDownLatch countDownLatch = new CountDownLatch(600_000);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    nums.get().add(System.currentTimeMillis());
                    countDownLatch.countDown();
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        Thread thread = new Thread(() -> {
            while (true) {
                if (!nums.get().isEmpty()) {
                    try {
                        // 读/写并发，抛出 java.util.ConcurrentModificationException
                        // 写/写并发，ArrayList 出现 null 元素
                        for (Long l : nums.getAndSet(Lists.newArrayList())) {
                            if (l == null) {
                                System.out.println(l);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        countDownLatch.await(65, TimeUnit.SECONDS);
    }
}
