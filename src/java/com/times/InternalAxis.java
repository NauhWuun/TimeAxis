package com.times;

import com.times.RowCols.RowColumn;

public class InternalAxis
{
    private String colName;
    private RowColumn rowColumn;
    private long timestamp;

    public InternalAxis(final String colName, RowColumn rowColumn) {
        this.colName = colName;
        this.rowColumn = rowColumn;
        this.rowColumn.getCreateTimestamp().getRight();
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
