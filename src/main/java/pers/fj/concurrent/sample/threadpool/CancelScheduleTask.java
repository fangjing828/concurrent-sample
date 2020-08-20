package pers.fj.concurrent.sample.threadpool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fang_j on 2020/08/20.
 */
public class CancelScheduleTask {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(5);
        AtomicLong counter = new AtomicLong();
        ScheduledFuture future = executorService.scheduleWithFixedDelay(() -> {
            System.out.println(counter.incrementAndGet());
            countDownLatch.countDown();
        }, 1000, 1000, TimeUnit.MILLISECONDS);

        countDownLatch.await();

        //终止定期 job
        future.cancel(false);
    }
}
