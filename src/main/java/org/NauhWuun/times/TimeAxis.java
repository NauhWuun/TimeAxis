package org.NauhWuun.times;

import java.io.IOException;
import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;

import org.rocksdb.RocksDBException;

public class TimeAxis implements Closeable
{
    private static final String FILENAME = "./time.axis";
    static RockDB db;
    static final ExecutorService executorService = Executors.newFixedThreadPool(2 << 3);
    private CountMinSketch cms;

    public TimeAxis() {
        try {
            db = RockDB.getDatabase(FILENAME);
            cms = CountMinSketch.deserialize(db.get(RockDB.TYPE_INDEX, "index".getBytes()));
            if (cms == null) {
                cms = new CountMinSketch();
            }
        } catch (RocksDBException e) {
            e.fillInStackTrace();
        }
    }

    public void push(String key, String value) {
        cms.setString(key);
        Mapper.add(KEY.Builder(key), VALUE.Builder(value));
    }

    public static Map<Object, Object> getNow() {
        return Reduce.divergence(Bytes.convertToByteArray(System.currentTimeMillis()));
    }

    public boolean contains(String key) {
        return cms.getEstimatedCountString(key) > 0;
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }

    @Override
    public void close() throws IOException {
        byte[] serCMS = CountMinSketch.serialize(cms);

        try {
            db.put(RockDB.TYPE_INDEX, "index".getBytes(), serCMS);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }
}