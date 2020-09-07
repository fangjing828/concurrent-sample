package pers.fj.concurrent.sample.wal;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 2020/09/04.
 */
public class WAL {
    public long writeEntry(byte[] data) {
        return 0;
    }

    List<WALEntry> readAll() {
        return Lists.newArrayList();
    }
}
