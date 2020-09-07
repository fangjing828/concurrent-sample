package pers.fj.concurrent.sample.segmentedlog;

/**
 * Created by fang_j on 2020/09/04.
 */
public class WALEntry {
    private long entryId;

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }
}
