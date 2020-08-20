package pers.fj.concurrent.sample.collection;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fang_j on 2020/08/20.
 */
public class ForEachByIteratorSample {
    public static void main(String[] args) {
        AtomicReference<List<Long>> futures = new AtomicReference<>(Collections.synchronizedList(Lists.newArrayList()));

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (!futures.get().isEmpty()) {
                    Iterator<Long> iterator = futures.getAndSet(Collections.synchronizedList(Lists.newArrayList())).iterator();
                    while (iterator.hasNext()) {
                        System.out.println(iterator.next());
                    }
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        AtomicLong counter = new AtomicLong();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                while (true) {
                    futures.get().add(counter.incrementAndGet());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
