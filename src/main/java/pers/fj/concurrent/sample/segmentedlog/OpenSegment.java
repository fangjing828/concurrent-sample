package pers.fj.concurrent.sample.segmentedlog;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fang_j on 2020/09/04.
 */
public class OpenSegment extends WALSegment {
    private final AtomicLong id;
    private final long startId;

    public OpenSegment(long startId, String dir, String fileName) {
        super(dir, fileName);
        this.startId = startId;
        this.id = new AtomicLong(startId);
    }

    public long writeEntry(WALEntry entry) {
        entry.setEntryId(id.incrementAndGet() - 1);
        try {
            getDataOutputStream().writeUTF(gson.toJson(entry));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entry.getEntryId();
    }

    public long size() {
        return id.get() - startId;
    }

    public void flush() throws IOException {
        os.flush();

    }

    public void close() throws IOException {
        os.close();
    }

    public long getLastLogEntryId() {
        return id.get() - 1;
    }
}
