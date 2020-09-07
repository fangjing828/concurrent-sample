package pers.fj.concurrent.sample.segmentedlog;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fang_j on 2020/09/04.
 */
public class WAL {
    private final WALConfig config;
    private final List<OpenSegment> sortedSavedSegments;
    private volatile OpenSegment openSegment;

    public WAL(WALConfig config) {
        this.config = config;
        this.sortedSavedSegments = Lists.newArrayList();
    }

    public long writeEntry(WALEntry entry) {
        maybeRoll();
        return openSegment.writeEntry(entry);
    }

    private void maybeRoll() {
        if (openSegment == null || openSegment.size() >= config.getMaxLogSize()) {
            try {
                long lastId = 0;
                if (openSegment != null) {
                    openSegment.flush();
                    openSegment.close();
                    lastId = openSegment.getLastLogEntryId();
                }
                openSegment = WALSegment.open(lastId, config.getWalDir());
                sortedSavedSegments.add(openSegment);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<WALEntry> readFrom(Long startIndex) {
        List<WALSegment> segments = getAllSegmentsContainingLogGreaterThan(startIndex);
        return readWalEntriesFrom(startIndex, segments);
    }

    private List<WALSegment> getAllSegmentsContainingLogGreaterThan(Long startIndex) {
        List<WALSegment> segments = Lists.newArrayList();
        //Start from the last segment to the first segment with starting offset less than startIndex
        //This will get all the segments which have log entries more than the startIndex
        for (int i = sortedSavedSegments.size() - 1; i >= 0; i--) {
            WALSegment walSegment = sortedSavedSegments.get(i);
            segments.add(walSegment);

            if (walSegment.getBaseOffset() <= startIndex) {
                break; // break for the first segment with baseoffset less than startIndex
            }
        }

        if (openSegment.getBaseOffset() <= startIndex) {
            segments.add(openSegment);
        }

        return segments;
    }

    private List<WALEntry> readWalEntriesFrom(long startIndex, List<WALSegment> segments) {
        List<WALEntry> entries = Lists.newArrayList();
        for (WALSegment segment : segments) {
            entries.addAll(segment.readAll().stream().filter(entry -> entry.getEntryId() > startIndex).collect(Collectors.toList()));
        }
        return entries;
    }

    public static void main(String[] args) {
        List<File> files = Lists.newArrayList(new File(WALConfig.DEFAULT_DIR));
        while (!files.isEmpty()) {
            File file = files.remove(0);
            if (file.exists()) {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        files.add(f);
                    }
                } else {
                    file.delete();
                }
            }
        }
        WAL wal = new WAL(new WALConfig());
        for (int i = 0; i < 10000; i++) {
            wal.writeEntry(new WALEntry());
        }

        List<WALEntry> entries = Lists.newArrayList();
        wal.readFrom(500L).forEach(entry -> {
            entries.add(entry);
            System.out.println(entry.getEntryId());
        });
    }
}
