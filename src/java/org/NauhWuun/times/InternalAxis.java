package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.RowColumn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InternalAxis
{
    private String colName;
    private RowColumn rowColumn;
    private long timestamp;

    public InternalAxis(final String colName, RowColumn rowColumn) {
        this.colName = colName;
        this.rowColumn = rowColumn;
        this.timestamp = Long.parseLong(RowColumn.createdateTime());
    }

    public RowColumn getRowColumn() {
        return this.rowColumn;
    }

    public String getColName() {
        return this.colName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}