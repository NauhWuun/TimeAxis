package org.NauhWuun.times;

import org.NauhWuun.times.Blocks.Block;
import org.NauhWuun.times.RowCols.RowColumn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimeAxis
{
    private static final ScheduledExecutorService freezing = Executors.newSingleThreadScheduledExecutor();
    private static final int cacheSize =  1024 * 1024 * 5 * 1;

    private static Map<String, InternalAxis> maps = new LinkHashMap<String, Internal>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Internal> eldest) {
            return size() > cacheSize;
        }
    };

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
        maps.get(name).getRowColumn().put(tag, value);
        return this;
    }

    public TimeAxis addTagValues(final String name, Map<? extends K, ? extends V> map) {
        maps.get(name).getRowColumn().putAll(map);
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

    public void shutDown() {
        System.out.println("ShutDown Freezing... \r\n");

        freezing.shutdownNow();

        try {
            autoFreezing();
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.notify();
        }
    }

    private static void autoFreezing() {
        if (maps.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        maps.entrySet().parallelStream().forEach(entry -> sb.append(entry.getValue().getRowColumn().toString()));

        Block timeBlock = new Block(sb.toString());
        Path newFilePath = Paths.get("." + "/TimeBlocks/" + String.valueOf(Objects.requireNonNull(timeBlock).getTimeStamp()));

        try {
            Files.createFile(newFilePath);
            Files.write(newFilePath, timeBlock.toBytes(), StandardOpenOption.APPEND);
            maps.clear();
        } catch (IOException e) {
            e.getSuppressed();
        }
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase";
    }
}