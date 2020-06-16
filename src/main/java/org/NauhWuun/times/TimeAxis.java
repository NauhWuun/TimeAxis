package org.NauhWuun.times;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import org.rocksdb.RocksDBException;

public class TimeAxis implements Closeable 
{
    static final String FILENAME = "./time.axis";
    static RockDB db;
    static volatile long fixRateTime = 0;
    static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    
    CountMinSketch cms;

    public TimeAxis() {
        try {
            db = RockDB.getDatabase(FILENAME);
            if (db == null)
                throw new IllegalArgumentException("cann't open local database file...");

            cms = CountMinSketch.deserialize(db.get(RockDB.TYPE_INDEX, "index".getBytes()));
        } catch (RocksDBException | NullPointerException e) {
            if (cms == null)
                cms = new CountMinSketch();
        }

        executorService.scheduleAtFixedRate(Mapper::Clone, 0, 30 /* 30s Wrapper Times */, TimeUnit.SECONDS);
    }

    public void push(String key, String value) {
        cms.setString(key);
        Mapper.add(KEY.Builder(key), VALUE.Builder(value));
    }

    /**
     *
     * @param secondTime: 30 seconds per 30/60/180/... seconds
     * @return 30s block key/value data
     */
    public static Map<KEY, VALUE> poll(long secondTime) {
        if (secondTime < 30 || secondTime >= Long.MAX_VALUE)
            throw new IllegalArgumentException("time value is failed");

        return Objects.requireNonNull(Reduce.divergence(Bytes.convertToByteArray(secondTime)));
    }

    public static Map<KEY, VALUE> pollLast() {
        return Reduce.divergence(db.getLast(RockDB.TYPE_TRANSACTIONS).key());
    }

    public static long pollLastKey() {
        return Bytes.ConvertBytesToLong(db.getLast(RockDB.TYPE_TRANSACTIONS).key());
    }

    public static long pollFirstKey() {
        return Bytes.ConvertBytesToLong(db.getFirst(RockDB.TYPE_TRANSACTIONS, null).key());
    }

    public static long getCount() { return db.getCount(); }

    public boolean contains(String key) { return cms.getEstimatedCountString(key) == 0; }

    public static String timeToHour(long dataTime) {
        if (dataTime < 60)
            return dataTime + "秒";
        return new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒").format(
                new Date(Long.parseLong(String.valueOf(dataTime))));
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }

    @Override
    public void close()  {
        byte[] serCMS = CountMinSketch.serialize(cms);
        try {
            db.put(RockDB.TYPE_INDEX, "index".getBytes(), serCMS);
        } catch (RocksDBException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
