package org.NauhWuun.times;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.NauhWuun.times.Blocks.Block;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.rocksdb.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public final class TimeAxis implements AutoCloseable
{
    private static final ScheduledExecutorService freezing = Executors.newSingleThreadScheduledExecutor();
    private static final Cache<String, RowCol> maps = Caffeine.newBuilder()
            .maximumSize(1024 * 1024 * 500)
            .build();

    private static final int WINDOW_WHEEL = 30;
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
        Objects.requireNonNull(maps.getIfPresent(name)).Put(tag, value);
    }

    public static List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
        return new CSVParser(Files.newBufferedReader(Paths.get(csvFile)), skipHeader
                ? CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                : CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim()).getRecords();
    }

    public static void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
        new CSVPrinter(Files.newBufferedWriter(Paths.get(csvFile)), CSVFormat.DEFAULT.withHeader(fileHeader)).flush();
    }

    protected static HashMap<String, RowCol> InvertedMap() {
		Set<Entry<String, RowCol>> set = maps.asMap().entrySet();
		ArrayList<Entry<String, RowCol>> arrayList = new ArrayList<>(set);

        arrayList.sort((arg0, arg1) ->
            (arg1.getValue().getCurrentTimestamp())
                .compareTo(arg0.getValue().getCurrentTimestamp()));

        LinkedHashMap<String, RowCol> map = new LinkedHashMap<>();
        arrayList.forEach(k -> map.put(k.getKey(), k.getValue()));
		return map;
    }

    private static void autoFreezing() {
        if (maps.asMap().isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        maps.asMap().forEach((k, v) -> {
            sb.append(k);
            sb.append(v.getRowColumnID());
            sb.append(v.getCreateTimestamp().getRight());
            sb.append(v.Size().getRight());

            v.getKeyValues().forEach((k1, v1) -> {
                sb.append(k1);
                sb.append(v1);
            });
        });

        try {
            Block timeBlock = new Block(sb.toString().getBytes());
            rocksDB.put(timeBlock.getTimeStamp().getBytes(), timeBlock.toBytes());
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public static RocksIterator getDBIterator() { return iter; }

    @Override
    public void close() {
        System.out.println("ShutDown Time-Axis... \r\n");
        freezing.shutdownNow();
        autoFreezing();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}