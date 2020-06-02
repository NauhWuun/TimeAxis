package org.NauhWuun.times;

import org.rocksdb.*;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class RockDB
{
    static {
        RocksDB.loadLibrary();
    }

    private static final ConcurrentHashMap<String, RockDB> dbInstance = new ConcurrentHashMap<>();

    public static RockDB getDatabase(String db) throws RocksDBException {
        RockDB rockDB = dbInstance.get(db);
        if (rockDB == null) {
            synchronized (RockDB.class) {
                rockDB = dbInstance.get(db);
                if (rockDB == null) {
                    rockDB = new RockDB(db);
                    dbInstance.put(db, rockDB);
                }
            }
        }
        return rockDB;
    }

    private RocksDB database;

    private RockDB(String db) throws RocksDBException {
        File file = new File(db);
        if (! file.exists()) {
            file.mkdirs();
        }

        database = RocksDB.open(db);
    }

    public RocksDB getDatabase() {
        return database;
    }

    public RocksIterator getIter() {
        RocksIterator iterator = database.newIterator();
        iterator.seekToFirst();
        return iterator;
    }

    public RocksIterator getFirst( byte[] _0) {
        RocksIterator iterator = getIter();
        if (_0 != null) {
            iterator.seek(_0);
        } else {
            iterator.seekToFirst();
        }
        return iterator;
    }

    public RocksIterator getLast() {
        RocksIterator iterator = getIter();
        iterator.seekToLast();
        return iterator;
    }

    public byte[] get(byte[] key) throws RocksDBException {
        return database.get(key);
    }

    public void put(byte[] key, byte[] value) throws RocksDBException {
        database.put(key, value);
    }

    public void delete(byte[] key) throws RocksDBException {
        delete(key);
    }

    public long getCount() {
        return getCount((byte) 0);
    }

    public long getCount(int columnFamily) {
        long count = 0;
        RocksIterator iterator = getIter();
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            count++;
        }
        return count;
    }

    public void close() {
        database.close();
    }
}
