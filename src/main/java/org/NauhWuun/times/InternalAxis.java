package org.NauhWuun.times;

import org.NauhWuun.times.RowCols.RowColumn;

import java.io.Serializable;

public class InternalAxis implements Serializable
{
    private final String colName;
    private final RowColumn rowColumn;

    public InternalAxis(final String colName, final RowColumn rowColumn) {
        this.colName = colName;
        this.rowColumn = rowColumn;
    }

    public RowColumn getRowColumn() {
        return this.rowColumn;
    }

    public String getColName() {
        return this.colName;
    }
}