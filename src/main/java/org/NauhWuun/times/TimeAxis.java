package org.NauhWuun.times;

import org.rocksdb.RocksDBException;

public final class TimeAxis 
{
    private static final STree maps = new STree();
    private static final String dbName = "TimeAxisDataBase";
    private static RockDB rocksDB;

    public TimeAxis() {
        try {
            rocksDB = RockDB.getDatabase(dbName);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void push(String key, String value) {
        maps.add(KEY.Builder(key).getKey().getBytes(), VALUE.Builder(value).getValue().getBytes());
    }

    public void Remove(String key) {
        maps.delete(key);
    }

    public static RockDB getDBInstance() { return rocksDB; }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}