package org.NauhWuun.times;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

public class TimeAxisBench
{
	private static final int TEST_NUMBER = Integer.MAX_VALUE - 1;

	TimeAxis timeAxis;

	TimeAxisBench() {
		timeAxis = new TimeAxis();
	}

	void addValue(final String name, final String tag, final Object value) throws InterruptedException {
		timeAxis.addValue(name, tag, value);
	}

	List<CSVRecord> csvReader(final String csvFile, final String[] fileHeader, boolean skipHeader) throws IOException {
		return TimeAxis.csvReader(csvFile, fileHeader, skipHeader);
	}

	void csvWriter(final String csvFile, final String[] fileHeader, List<String[]> content) throws IOException {
		TimeAxis.csvWriter(csvFile, fileHeader, content);
	}

	public static void main(String... main) throws InterruptedException {
		TimeAxisBench tx = new TimeAxisBench();
		for (int i = 0; i < TEST_NUMBER; i++) {
			tx.addValue("test" + i, "test " + i, "test " + i);
		}
	}
}