package pers.fj.concurrent.sample.collection;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by fang_j on 2020/12/28.
 */
public class TransferQueueTest {
    @Test
    public void whenUseOneProducerAndNoConsumer_thenShouldFailWithTimeout() throws InterruptedException {
        // given
        TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);
        Producer producer = new Producer(transferQueue, "1", 3);

        // when
        exService.execute(producer);

        // then
        exService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        exService.shutdown();

        assertEquals(producer.numberOfProducedMessages.intValue(), 0);
    }

    @Test
    public void whenUseOneConsumerAndOneProducer_thenShouldProcessAllMessages()
            throws InterruptedException {
        // given
        TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);
        Producer producer = new Producer(transferQueue, "1", 3);
        Consumer consumer = new Consumer(transferQueue, "1", 3);

        // when
        exService.execute(producer);
        exService.execute(consumer);

        // then
        exService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        exService.shutdown();

        assertEquals(producer.numberOfProducedMessages.intValue(), 3);
        assertEquals(consumer.numberOfConsumedMessages.intValue(), 3);
    }

    @Test
    public void whenMultipleConsumersAndProducers_thenProcessAllMessages()
            throws InterruptedException {
        // given
        TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(3);
        Producer producer1 = new Producer(transferQueue, "1", 3);
        Producer producer2 = new Producer(transferQueue, "2", 3);
        Consumer consumer1 = new Consumer(transferQueue, "1", 3);
        Consumer consumer2 = new Consumer(transferQueue, "2", 3);

        // when
        exService.execute(producer1);
        exService.execute(producer2);
        exService.execute(consumer1);
        exService.execute(consumer2);

        // then
        exService.awaitTermination(10_000, TimeUnit.MILLISECONDS);
        exService.shutdown();

        assertEquals(producer1.numberOfProducedMessages.intValue(), 3);
        assertEquals(producer2.numberOfProducedMessages.intValue(), 3);
    }

    static class Producer implements Runnable {
        private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
        private TransferQueue<String> transferQueue;
        private String name;
        private int numberOfMessageToProducer;
        private AtomicInteger numberOfProducedMessages = new AtomicInteger();

        public Producer(TransferQueue<String> transferQueue, String name, int numberOfMessageToProducer) {
            this.transferQueue = transferQueue;
            this.name = name;
            this.numberOfMessageToProducer = numberOfMessageToProducer;
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfMessageToProducer; i++) {
                try {
                    LOG.debug("Producer: " + name + " is waiting to transfer...");
                    boolean added
                            = transferQueue.tryTransfer("A" + i, 4000, TimeUnit.MILLISECONDS);
                    if (added) {
                        numberOfProducedMessages.incrementAndGet();
                        LOG.debug("Producer: " + name + " transferred element: A" + i);
                    } else {
                        LOG.debug("can not add an element due to the timeout");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
        private TransferQueue<String> transferQueue;
        private String name;
        private int numberOfMessageToConsume;
        private AtomicInteger numberOfConsumedMessages = new AtomicInteger();

        public Consumer(TransferQueue<String> transferQueue, String name, int numberOfMessageToConsume) {
            this.transferQueue = transferQueue;
            this.name = name;
            this.numberOfMessageToConsume = numberOfMessageToConsume;
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfMessageToConsume; i++) {
                try {
                    LOG.debug("Consumer: " + name + " is waiting to take element...");
                    String element = transferQueue.take();
                    logProcessing();
                    LOG.debug("Consumer: " + name + " received element: " + element);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void logProcessing() throws InterruptedException {
            numberOfConsumedMessages.incrementAndGet();
            Thread.sleep(500);
        }
    }
}
