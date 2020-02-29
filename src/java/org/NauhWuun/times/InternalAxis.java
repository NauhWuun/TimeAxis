package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.RowColumn;
import org.NauhWuun.times.Until.TimeStamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InternalAxis
{
    private String colName;
    private RowColumn rowColumn;
    private long timestamp;

    public InternalAxis(final String colName, RowColumn rowColumn) throws ParseException {
        this.colName = colName;
        this.rowColumn = rowColumn;
        this.rowColumn.getCreateTimestamp().getRight();
        this.timestamp = TimeStamp.date2Stamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public RowColumn getRowColumn() {
        return this.rowColumn;
    }

    public String getcolName() {
        return this.colName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
