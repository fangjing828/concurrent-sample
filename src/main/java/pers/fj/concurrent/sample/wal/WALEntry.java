package pers.fj.concurrent.sample.wal;

/**
 * Created by fang_j on 2020/09/04.
 */
public class WALEntry {
    private final long entryId;
    private final byte[] data;
    private final EntryType entryType;
    private long timestamp;

    public WALEntry(long entryId, byte[] data, EntryType entryType) {
        this.entryId = entryId;
        this.data = data;
        this.entryType = entryType;
        this.timestamp = System.currentTimeMillis();
    }

    public long getEntryId() {
        return entryId;
    }

    public byte[] getData() {
        return data;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
