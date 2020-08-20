package pers.fj.concurrent.sample.collection;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fang_j on 2020/08/20.
 */
public class ForEachByStreamSample {
    //TODO why not throw ConcurrentModificationException in use stream?
    public static void main(String[] args) {
        AtomicReference<List<Long>> futures = new AtomicReference<>(Collections.synchronizedList(Lists.newArrayList()));

        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                if (!futures.get().isEmpty()) {
                    futures.getAndSet(Collections.synchronizedList(Lists.newArrayList())).forEach(System.out::println);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        AtomicLong counter = new AtomicLong();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    futures.get().add(counter.incrementAndGet());
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}