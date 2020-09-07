package pers.fj.concurrent.sample.segmentedlog;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by fang_j on 2020/09/04.
 */
public class WALSegment {
    private static final String LOG_PREFIX = "wal";
    private static final String LOG_SUFFIX = "log";
    private static final Joiner LOG_JOINER = Joiner.on('_');
    private static final Splitter LOG_SPLITTER = Splitter.on('_');

    public static OpenSegment open(long lastId, String dir) {
        long startId = lastId + 1;
        return new OpenSegment(startId, dir, createFileName(startId));
    }

    public static String createFileName(Long startIndex) {
        return LOG_JOINER.join(LOG_PREFIX, startIndex, LOG_SUFFIX);
    }

    public static Long getBaseOffsetFromFileName(String fileName) {
        List<String> splitters = LOG_SPLITTER.splitToList(fileName);
        if (splitters.size() == 3
                && splitters.get(0).equalsIgnoreCase(LOG_PREFIX)
                && splitters.get(2).equalsIgnoreCase(LOG_SUFFIX)) {
            return Long.parseLong(splitters.get(1));
        }

        return -1l;
    }

    private final Object lock = new Object();
    private final Object readLock = new Object();
    private final String fileName;
    protected final String filePath;
    protected final Gson gson;
    protected volatile DataOutputStream os;
    protected volatile DataInputStream is;


    public WALSegment(String dir, String fileName) {
        this.fileName = fileName;
        this.filePath = dir + "/" + fileName;
        this.gson = new Gson();
    }

    public long getBaseOffset() {
        return getBaseOffsetFromFileName(fileName);
    }

    public List<WALEntry> readAll() {
        List<WALEntry> entries = Lists.newArrayList();
        try {
            DataInputStream is = getDataInputStream();
            while (!Thread.currentThread().isInterrupted()) {
                entries.add(gson.fromJson(is.readUTF(), WALEntry.class));
            }
        } catch (EOFException e) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    protected DataOutputStream getDataOutputStream() throws IOException {
        if (os == null) {
            synchronized (lock) {
                if (os == null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        if (!file.createNewFile()) {
                            throw new IllegalStateException("Create file failed." + filePath);
                        }
                    }

                    os = new DataOutputStream(new FileOutputStream(filePath));
                }
            }
        }
        return os;
    }

    protected DataInputStream getDataInputStream() throws IOException {
        if (is == null) {
            synchronized (readLock) {
                if (is == null) {
                    is = new DataInputStream((new FileInputStream(filePath)));
                }
            }
        }
        return is;
    }


}
