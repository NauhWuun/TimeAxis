package org.NauhWuun.times;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.NauhWuun.times.Blocks.Block;
import org.NauhWuun.times.RowCols.Column;
import org.NauhWuun.times.RowCols.Rows;
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
    private static final Cache<String, Column> maps = Caffeine.newBuilder()
            .maximumSize(1024 * 1024 * 500)
            .build();

    private static final int WINDOW_WHEEL = 5;
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

    public void createColumnFamily(final Column Column) {
        maps.put(Column. Column);
    }

    public void addValue(final String name, final String tag, final Object value) {
        Objects.requireNonNull(maps.getIfPresent(name)).getRows().Put(tag, value);
    }

    public static List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
        return new CSVParser(Files.newBufferedReader(Paths.get(csvFile)), skipHeader
                ? CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                : CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim()).getRecords();
    }

    public static void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
        new CSVPrinter(Files.newBufferedWriter(Paths.get(csvFile)), CSVFormat.DEFAULT.withHeader(fileHeader)).flush();
    }

    protected static HashMap<String, Column> InvertedMap() {
		Set<Entry<String, Column>> set = maps.asMap().entrySet();
		ArrayList<Entry<String, Column>> arrayList = new ArrayList<>(set);

        arrayList.sort((arg0, arg1) ->
            (arg1.getValue().getRows().getCurrentTimestamp())
                .compareTo(arg0.getValue().getRows().getCurrentTimestamp()));

        LinkedHashMap<String, Column> map = new LinkedHashMap<>();
        arrayList.forEach(k -> map.put(k.getKey(), k.getValue()));
		return map;
    }

    private static void autoFreezing() {
        if (maps.asMap().isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        HashMap<String, Column> map = InvertedMap();
        Collection<Column> set = map.values();

        while (set.iterator().hasNext()) {
            Column axis = set.iterator().next();

            while (axis.getRows().)
            sb.append(axis.getColName());
            sb.append(axis.getRows().getRowColumnID());
            sb.append(axis.getRows().getCreateTimestamp().getRight());
            sb.append(axis.getRows().Size().getRight());
            sb.append(axis.getRows().KeySet().getRight());
            sb.append(axis.getRows().Values().getRight());
        }

        Block timeBlock = new Block(sb.toString().getBytes());

        try {
            rocksDB.put(timeBlock.getTimeStamp().getBytes(), timeBlock.toBytes());
            maps.asMap().forEach((k, v) -> map.forEach((k1, v1) -> {
                if (k1.contains(k) && v1.getRows().equals(v.getRows()))
                    maps.asMap().remove(k, v);
            }));
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