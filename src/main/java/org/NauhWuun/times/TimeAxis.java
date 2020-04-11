package org.NauhWuun.times;

import org.NauhWuun.times.Blocks.Block;
import org.NauhWuun.times.RowCols.RowColumn;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
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

    public static List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException{
        CSVFormat format;

        if (skipHeader) {
            format = CSVFormat.DEFAULT.withHeader(fileHeader).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        } else {
            format = CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim();
        }

        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
        CSVParser csvParser = new CSVParser(reader, format);
        return csvParser.getRecords();
    }

    public static void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFile));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(fileHeader));

        for (String[] c : content) {
            csvPrinter.printRecord(Arrays.asList(c));
        }
        csvPrinter.flush();
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

        StringBuilder sb = new StringBuilder();
        maps.entrySet().parallelStream().forEach(entry -> {
            sb.append(entry.getValue().getRowColumn().getRowColumnID());
            sb.append(entry.getValue().getRowColumn().getCreateTimestamp());
            sb.append(entry.getValue().getRowColumn().getRowColumnDescribe());
            sb.append(entry.getValue().getRowColumn().getRowColumnName());
            sb.append(entry.getValue().getRowColumn().Size());
            sb.append(entry.getValue().getRowColumn().getIterator().getRight().next().getKey());
            sb.append(entry.getValue().getRowColumn().getIterator().getRight().next().getValue());
        });

        Block timeBlock = new Block(sb.toString().getBytes());
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