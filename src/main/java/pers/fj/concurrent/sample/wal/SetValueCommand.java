package pers.fj.concurrent.sample.wal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

/**
 * Created by fang_j on 2020/09/04.
 */
public class SetValueCommand implements Command {
    private final String key;
    private final String value;

    public SetValueCommand(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(os);
            dataOutputStream.writeInt(Command.SetValueType);
            dataOutputStream.writeUTF(key);
            dataOutputStream.writeUTF(value);
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
