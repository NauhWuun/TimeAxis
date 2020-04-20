package org.NauhWuun.times.RowCols;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Column
{
    private static final Map<String, Rows> rows = new ConcurrentHashMap<>();

    public Column(final String colName) {
        rows.put(colName, new Rows());
    }

    public Rows getRows(String name) { return rows.get(name); }
}