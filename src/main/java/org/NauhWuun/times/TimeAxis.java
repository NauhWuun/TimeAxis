package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.RowColumn;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public final class TimeAxis implements AutoCloseable
{
    private static final ScheduledExecutorService freezing = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, InternalAxis> maps = new ConcurrentSkipListMap<>();
    private static final int WINDOW_WHEEL = 60;

    public TimeAxis() {
        freezing.scheduleAtFixedRate(TimeAxis::autoFreezing, 0, WINDOW_WHEEL, TimeUnit.SECONDS);
    }

    public TimeAxis createRowColumn(final String describe, final String name) {
        RowColumn rc = new RowColumn(describe, name);
        InternalAxis ia = new InternalAxis(name, rc);

        maps.put(name, ia);
        return this;
    }

    public TimeAxis addTagValue(final String name, final String tag, final Object value) {
        maps.get(name).getRowColumn().Put(tag, value);
        return this;
    }

    public TimeAxis addTagValues(final String name, Map<?, ?> map) {
        maps.get(name).getRowColumn().PutAll(map);
        return this;
    }

    public static Map<String, Object> loadingCSVData(final String fileName) {

    }

    public static void saveCSVData(final String fileName) {

    }

    public static HashMap<String, InternalAxis> InvertedMap() {
		Set<Entry<String, InternalAxis>> set = maps.entrySet();
		ArrayList<Entry<String, InternalAxis>> arrayList = new ArrayList<>(set);
 
        arrayList.sort((arg0, arg1) -> 
            (arg1.getValue().getRowColumn().getCurrentTimestamp())
                .compareTo(arg0.getValue().getRowColumn().getCurrentTimestamp()));
		
        LinkedHashMap<String, InternalAxis> map = new LinkedHashMap<>();
        
        for (Entry<String, InternalAxis> entry : arrayList) {
            map.put(entry.getKey(), entry.getValue());
        }
 
		return map;
    }

    private static void autoFreezing() {
        if (maps.isEmpty()) return;

//        StringBuilder sb = new StringBuilder();
//        maps.entrySet().parallelStream().forEach(entry -> sb.append(entry.getValue().getRowColumn().toString()));
//
//        Block timeBlock = new Block(sb.toString());

//        Path newFilePath = Paths.get("." + "/TimeBlocks/" + Objects.requireNonNull(timeBlock).getTimeStamp());
//
//        try {
//            Files.createFile(newFilePath);
//            Files.write(newFilePath, timeBlock.toBytes(), StandardOpenOption.APPEND);
//            maps.clear();
//        } catch (IOException e) {
//            e.getSuppressed();
//        }
    }

    @Override
    public void close() {
        System.out.println("ShutDown Time-Axis... \r\n");
        freezing.shutdownNow();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}