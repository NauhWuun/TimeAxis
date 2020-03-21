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
    private static Map<String, InternalAxis> maps, cacheMap;

    public TimeAxis() {
        maps = new HashMap<>();
        cacheMap = new ConcurrentHashMap<>();
        freezing.scheduleAtFixedRate(TimeAxis::autoFreezing, 0, 5, TimeUnit.SECONDS);
    }

    public TimeAxis Builder() {
        return this;
    }

    public TimeAxis createRowColumn(final String describe, final String name) {
        RowColumn rc = new RowColumn(describe, name);
        InternalAxis ia = new InternalAxis(name, rc);

        cacheMap.put(name, ia);
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
            autoFreezing();

            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.notify();
        }
    }

    private static void autoFreezing() {
        StringBuilder sb = new StringBuilder();
        cacheMap.entrySet().parallelStream().forEach(entry -> sb.append(entry.getValue().getRowColumn().toString()));

        if (sb.length() < 0) {
            return;
        }

        Block timeBlock = new Block(sb.toString());
        Path newFilePath = Paths.get(String.valueOf(Objects.requireNonNull(timeBlock).getTimeStamp()));

        try {
            Files.createFile(newFilePath);
            Files.write(newFilePath, timeBlock.toBytes(), StandardOpenOption.APPEND);

            cacheMap.clear();
        } catch (IOException e) {
            e.getSuppressed();
        }
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase";
    }
}