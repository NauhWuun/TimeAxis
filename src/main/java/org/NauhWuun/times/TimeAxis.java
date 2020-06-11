package org.NauhWuun.times;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;

import org.rocksdb.RocksDBException;

public class TimeAxis implements Closeable
{
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
     * @param secondTime: 30 seconds per
     *                    30/60/180/... seconds
     * @return 30s block key/value data
     */
    public Map<Object, Object> poll(long secondTime) {
        if (secondTime < 30 || secondTime >= Long.MAX_VALUE)
            secondTime = 30;

        if (secondTime % 30 != 0)
            secondTime *= 30;

        return Reduce.divergence(Bytes.convertToByteArray(secondTime));
    }
    
    /**
     *
     * @param start times: current times/second
              end times: endof times/second
              per times: a unit of 30/s
     * @return start to end times all dat int maps
     */
    public Map<Object, Object> poll(long start, long end) {
        Map<?, ?> newMaps = new HashMap<?, ?>();
        for (; start < end; start += 30) {
            newMaps.putAll(this.poll(start));
        }
        return newMaps;
    }

    public boolean contains(String key) {
        return cms.getEstimatedCountString(key) > 0;
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
        }
    }
}
