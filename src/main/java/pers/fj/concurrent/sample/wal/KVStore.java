package pers.fj.concurrent.sample.wal;

import com.google.common.collect.Maps;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 2020/09/04.
 */
public class KVStore {
    private final Map<String, String> kv = Maps.newHashMap();
    private final WAL wal = new WAL();

    public KVStore() {

        this.applyLog();
    }

    public String get(String key) {
        return kv.get(key);
    }

    public void put(String key, String value) {
        appendLog(key, value);
        kv.put(key, value);
    }

    public void remove(String key) {
        appendLog(key);
        kv.remove(key);
    }

    public void appendLog(String key, String value) {
        wal.writeEntry(new SetValueCommand(key, value).serialize());
    }

    public void appendLog(String key) {
        wal.writeEntry(new RemoveValueCommand(key).serialize());
    }

    public void applyLog() {
        applyEntries(wal.readAll());
    }

    public void applyEntries(List<WALEntry> entries) {
        entries.forEach(entry -> {
            Command command = deserialize(new ByteArrayInputStream(entry.getData()));
            if (command instanceof SetValueCommand) {
                SetValueCommand setValueCommand = (SetValueCommand) command;
                kv.put(setValueCommand.getKey(), setValueCommand.getValue());
            }
        });
    }

    public static Command deserialize(InputStream is) {
        try {
            DataInputStream dataInputStream = new DataInputStream(is);
            int type = dataInputStream.readInt();
            if (type == Command.SetValueType) {
                return new SetValueCommand(dataInputStream.readUTF(), dataInputStream.readUTF());
            } else if (type == Command.RemoveValueType) {
                return new RemoveValueCommand(dataInputStream.readUTF());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SetValueCommand command = new SetValueCommand("key", "value");
        byte[] body = command.serialize();
        Command c = deserialize(new ByteArrayInputStream(body));
    }
}
