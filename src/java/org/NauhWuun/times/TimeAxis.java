package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.RowColumn;
import org.rocksdb.RocksDB;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimeAxis
{
    private static final ScheduledExecutorService freezing = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, InternalAxis> maps;

    private LocalDB timeDB;

    public TimeAxis() {
        timeDB = new LocalDB("TimeAxis.db");
        maps = new HashMap<>();

        freezing.scheduleAtFixedRate(TimeAxis::autoFreezing, 0, 3, TimeUnit.SECONDS);
    }

    public TimeAxis Builder() {
        return this;
    }

    public TimeAxis createRowColumn(final String describe, final String name) throws ParseException {
        RowColumn rc = new RowColumn(describe, name);
        InternalAxis ia = new InternalAxis(name, rc);

        maps.put(name, ia);
        return this;
    }

    public TimeAxis addTagValue(final String name, final String tag, Object value) {
        maps.get(name).getRowColumn().put(tag, value);
        return this;
    }

    public static HashMap<String, InternalAxis> InvertedMap(Map<String, InternalAxis> invertTheMap) {
		Set<Entry<String, InternalAxis>> set = invertTheMap.entrySet();
		ArrayList<Entry<String, InternalAxis>> arrayList = new ArrayList<>(set);
 
		arrayList.sort((arg0, arg1) -> (arg1.getValue().getRowColumn().getCurrentTimestamp())
                .compareTo(arg0.getValue().getRowColumn().getCurrentTimestamp()));
		
		LinkedHashMap<String, InternalAxis> map = new LinkedHashMap<>();
        for (Entry<String, InternalAxis> entry : arrayList) {
            map.put(entry.getKey(), entry.getValue());
        }
 
		return map;
    }

    public void shutDown() {
        System.out.println("ShutDown Freezing... \r\n");
        freezing.shutdownNow();

        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.notify();
        }
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase";
    }

    private static void autoFreezing() {
        Block block = new Block(maps.);
    }
}