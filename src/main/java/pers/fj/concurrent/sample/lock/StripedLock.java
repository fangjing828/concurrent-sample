package pers.fj.concurrent.sample.lock;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Striped;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Created by fang_j on 2020/12/28.
 */
public class StripedLock extends ConcurrentAccessExperiment {
    Striped<Lock> stripedLock;

    public StripedLock(int buckets) {
        stripedLock = Striped.lock(buckets);
    }

    protected Supplier<?> putSupplier(Map<String,String> map, int key) {
        return (()-> {
            int bucket = key % stripedLock.size();
            Lock lock = stripedLock.get(bucket);
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
            int bucket = key % stripedLock.size();
            Lock lock = stripedLock.get(bucket);
            lock.lock();
            try {
                return map.get("key" + key);
            } finally {
                lock.unlock();
            }
        });
    }
}
