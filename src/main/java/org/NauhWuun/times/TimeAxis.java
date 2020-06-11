package org.NauhWuun.times;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.*;

import org.rocksdb.RocksDBException;

public class TimeAxis implements Closeable {
    private static final String FILENAME = "./time.axis";
    static RockDB db;
    static volatile long fixRateTime = 0;
    static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private CountMinSketch cms;

    public TimeAxis() {
        try {
            db = RockDB.getDatabase(FILENAME);
            cms = CountMinSketch.deserialize(db.get(RockDB.TYPE_INDEX, "index".getBytes()));
        } catch (RocksDBException e) {
            e.fillInStackTrace();
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
    public static Map<Object, Object> poll(long secondTime) {
        if (secondTime < 30 || secondTime >= Long.MAX_VALUE)
            secondTime = 30;

        if (secondTime % 30 != 0)
            secondTime *= 30;

        return Reduce.divergence(Bytes.convertToByteArray(secondTime));
    }

    public static Map<Object, Object> pollLast() {
        return Reduce.divergence(db.getLast(RockDB.TYPE_TRANSACTIONS).key());
    }

    public static Map<Object, Object> getMin() {
        Map<Object, Object> maps = poll(0);
        return maps.isEmpty() ? null : (Map<Object, Object>) maps.entrySet().iterator().next();
    }

    public static Map<Object, Object> getMax() {
        Map<Object, Object> maps = pollLast();
        Field tail;
        try {
            tail = maps.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        return (Map<Object, Object>) tail.get(maps);
    }

    public static long getCount() {
        return db.getCount();
    }

    public boolean contains(String key) {
        return cms.getEstimatedCountString(key) > 0;
    }

    public static String timeToHour(long dataTime) {
        if (dataTime < 60)
            return dataTime + "分钟"; 

	long hour = Math.round(dataTime / 60);
	long minute = Math.round(dataTime - (hour * 60));
	return hour + "小时" + (minute == 0 ? "" : minute + "分钟");
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
