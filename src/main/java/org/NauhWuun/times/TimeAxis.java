package org.NauhWuun.times;

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

public final class TimeAxis implements AutoCloseable
{
    private static final Map<String, Rows> maps = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Rows> eldest) {
            return size() >= 1024000;
        }
    };

    @Deprecated
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
        options.setAtomicFlush(true);
        options.setCompressionType(CompressionType.SNAPPY_COMPRESSION);
        options.setCompactionStyle(CompactionStyle.UNIVERSAL);

        try {
            rocksDB = RocksDB.open(options, dbName);
            iter = rocksDB.newIterator();
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void addValue(final String name, String tag, Object value) {
        if (maps.get(name) != null && maps.get(name).Size() >= 1024000) {
            StringBuilder sb = new StringBuilder();
            maps.forEach((k, v) -> {
                sb.append(k);
                sb.append(v.getRowColumnID());
                sb.append(v.getCreateTimestamp().getRight());
                sb.append(v.Size());

                v.getKeyValues().forEach((k1, v1) -> {
                    sb.append(k1);
                    sb.append(v1);
                });
            });

            final Block timeBlock = new Block(sb.toString().getBytes());
            try {
                rocksDB.put(timeBlock.getTimeStamp().getBytes(), timeBlock.toBytes());
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
        }

        if (maps.get(name) == null) {
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

    @Override
    public synchronized void close() {
        System.out.println("ShutDown Time-Axis... \r\n");
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}