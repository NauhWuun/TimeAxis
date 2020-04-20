package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.Column;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeAxisBench
{
	private static final int TEST_NUMBER = 15;
	private static final int DELAY_TIMES = 1000;

	TimeAxis timeAxis;

	TimeAxisBench() {
		timeAxis = new TimeAxis();
	}

	void createColumn(Column Column) {
		timeAxis.createColumnFamily(Column);
	}

	void addValue(final String name, final String tag, final Object value) {
		timeAxis.addValue(name, tag, value);
	}

	void addAll(final String name, Map<?, ?> map) {
		timeAxis.addAll(name, map);
	}

	List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
		return TimeAxis.csvReader(csvFile, fileHeader, skipHeader);
	}

	void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
		TimeAxis.csvWriter(csvFile, fileHeader, content);
	}

	HashMap<String, Column> InvertedMap() {
		return TimeAxis.InvertedMap();
	}

	public static void main(String... main) throws InterruptedException {
		TimeAxisBench tx = new TimeAxisBench();

		tx.createColumn(new Column("test"));

		for (int i = 1; i < TEST_NUMBER; i++) {
			tx.addValue("test", "test1" + i, "test2" + i);
		}
	}
}