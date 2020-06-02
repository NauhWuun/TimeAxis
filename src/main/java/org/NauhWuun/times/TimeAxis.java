package org.NauhWuun.times;

import java.io.IOException;
import java.io.Closeable;
import java.util.concurrent.*;

import org.rocksdb.RocksDBException;

public final class TimeAxis implements Closeable
{
    private static final STree maps = new STree();
    private final CountMinSketch cms;

    private static final String FILENAME = "./time.axis";
    public static RockDB db;

    public static final ExecutorService executorService = Executors.newFixedThreadPool(2 << 3);

    public TimeAxis() {
        // cms = CountMinSketch.deserialize(serialized);

        try {
            db = RockDB.getDatabase(FILENAME);
        } catch (RocksDBException e) {
            e.fillInStackTrace();
        }
    }

    public void push(String key, String value) {
        cms.setString(key);
        maps.add(KEY.Builder(key), VALUE.Builder(value));
    }

    public String get() {
        
    }

    public boolean hasKey(String key) {
        return cms.getEstimatedCountString(key) > 0;
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }

    @Override
    public void close() throws IOException {
        byte[] serCMS = CountMinSketch.serialize(cms);
    }
}