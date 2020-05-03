package org.NauhWuun.times;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.rocksdb.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class TimeAxis
{
    private static final STree maps = new STree();

    private static final String dbName = "TimeAxisDataBase";
    private static RocksDB rocksDB;
    private RocksIterator iter;

    static {
        RocksDB.loadLibrary();
    }

    public TimeAxis() {
        Options options = new Options();
        options.setCreateIfMissing(true);
        options.setAtomicFlush(true);
        options.setCompressionType(CompressionType.SNAPPY_COMPRESSION);

        try {
            rocksDB = RocksDB.open(options, dbName);
            iter = rocksDB.newIterator();
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void push(Object key, Object value) {

    }

    public void Remove(final String key) {

    }

    public static RocksDB getDBInstance() { return rocksDB; }

    public static List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
        return new CSVParser(Files.newBufferedReader(Paths.get(csvFile)), skipHeader
                ? CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                : CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim()).getRecords();
    }

    public static void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
        new CSVPrinter(Files.newBufferedWriter(Paths.get(csvFile)), CSVFormat.DEFAULT.withHeader(fileHeader)).flush();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}