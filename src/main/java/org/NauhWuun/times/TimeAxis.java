package org.NauhWuun.times;

import org.NauhWuun.times.Blocks.Block;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.rocksdb.*;

import javax.swing.plaf.TableHeaderUI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public final class TimeAxis implements AutoCloseable
{
    private static final ScheduledExecutorService freezing = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, Rows> maps = new LinkedHashMap<>(102400) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Rows> eldest) {
            return size() >= 1000000000;
        }
    };

    private static final WeakHashMap<String, Rows> shadowMap = new WeakHashMap<>();

    private static final int WINDOW_WHEEL = 10;
    private static final String dbName = "TimeAxisDataBase";
    private static RocksDB rocksDB;
    private static RocksIterator iter;

    static {
        RocksDB.loadLibrary();
    }

    public TimeAxis() {
        Options options = new Options();
        options.setCreateIfMissing(true);
        options.setAllowMmapReads(true);
        options.setAllowMmapWrites(true);
        options.allowFAllocate();
        options.setAtomicFlush(true);
        options.setDbLogDir("./logs");
        options.setInfoLogLevel(InfoLogLevel.INFO_LEVEL);
        options.setCompressionOptions(new CompressionOptions().setLevel(2));

        try {
            rocksDB = RocksDB.open(options, dbName);
            iter = rocksDB.newIterator();
        } catch (RocksDBException e) {
            e.printStackTrace();
        }

        freezing.scheduleAtFixedRate(TimeAxis::autoFreezing, 0, WINDOW_WHEEL, TimeUnit.SECONDS);
    }

    public void addValue(final String name, final String tag, final Object value) {
        if (maps.size() == 1024) {
            System.out.println("system is fulllllllllllllll");
            shadowMap.putAll(maps);
        }

        if (! maps.containsKey(tag)) {
            maps.put(name, new Rows());
        }

        maps.get(name).Put(tag, value);
    }

    public static List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
        return new CSVParser(Files.newBufferedReader(Paths.get(csvFile)), skipHeader
                ? CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                : CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim()).getRecords();
    }

    public static void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
        new CSVPrinter(Files.newBufferedWriter(Paths.get(csvFile)), CSVFormat.DEFAULT.withHeader(fileHeader)).flush();
    }

    protected static HashMap<String, Rows> getSortMaps() {
		Set<Entry<String, Rows>> set = maps.entrySet();
		ArrayList<Entry<String, Rows>> arrayList = new ArrayList<>(set);

        arrayList.sort((arg0, arg1) ->
            (arg1.getValue().getCurrentTimestamp())
                .compareTo(arg0.getValue().getCurrentTimestamp()));

        LinkedHashMap<String, Rows> map = new LinkedHashMap<>();
        arrayList.forEach(k -> map.put(k.getKey(), k.getValue()));
		return map;
    }

    private static void autoFreezing() {
        if (shadowMap.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        shadowMap.forEach((k, v) -> {
            sb.append(k);
            sb.append(v.getRowColumnID());
            sb.append(v.getCreateTimestamp().getRight());
            sb.append(v.Size().getRight());

            v.getKeyValues().forEach((k1, v1) -> {
                sb.append(k1);
                sb.append(v1);
            });

            maps.remove(k, v);
            shadowMap.remove(k, v);
        });

        try {
            Block timeBlock = new Block(sb.toString().getBytes());
            rocksDB.put(timeBlock.getTimeStamp().getBytes(), timeBlock.toBytes());
            System.out.println("rock db okay!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public static RocksIterator getDBIterator() { return iter; }

    @Override
    public synchronized void close() {
        System.out.println("ShutDown Time-Axis... \r\n");
        freezing.shutdownNow();
        autoFreezing();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}