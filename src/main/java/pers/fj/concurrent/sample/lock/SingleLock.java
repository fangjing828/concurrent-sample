package pers.fj.concurrent.sample.lock;

import com.google.common.base.Supplier;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fang_j on 2020/12/28.
 */
public class SingleLock extends ConcurrentAccessExperiment {
    ReentrantLock lock;

    public SingleLock() {
        lock = new ReentrantLock();
    }

    protected Supplier<?> putSupplier(Map<String,String> map, int key) {
        return (()-> {
            lock.lock();
            try {
                return map.put("key" + key, "value" + key);
            } finally {
                lock.unlock();
            }
        });
    }

    protected Supplier<?> getSupplier(Map<String,String> map, int key) {
        return (()-> {
            lock.lock();
            try {
                return map.get("key" + key);
            } finally {
                lock.unlock();
            }
        });
    }
}
