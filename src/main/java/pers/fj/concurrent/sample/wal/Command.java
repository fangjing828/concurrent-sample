package pers.fj.concurrent.sample.wal;

/**
 * Created by fang_j on 2020/09/04.
 */
public interface Command {
    int SetValueType = 0;
    int RemoveValueType = 1;

    byte[] serialize();
}
