package org.NauhWuun.times;

import org.rocksdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RockDB
{
    public static final int TYPE_INDEX = 0x00;
    public static final int TYPE_TRANSACTIONS = 0x01;

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

    private final RocksDB database;
    private final List<ColumnFamilyHandle> cfh;
    private final List<ColumnFamilyDescriptor> cfd;
    private DBOptions options;

    private RockDB(String db) throws RocksDBException {
        File file = new File(db);
        if (! file.exists()) {
            file.mkdirs();
        }

        cfd = new ArrayList<>();
        cfd.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
        cfd.add(new ColumnFamilyDescriptor("index".getBytes(), new ColumnFamilyOptions()));
        cfd.add(new ColumnFamilyDescriptor("transaction".getBytes(), new ColumnFamilyOptions()));

        cfh = new ArrayList<>();

        options = new DBOptions()
                      .setCreateMissingColumnFamilies(true)
                      .setCreateIfMissing(true)
                      .setParanoidChecks(true)
                      .setMaxOpenFiles(2048);

        database = RocksDB.open(options, db, cfd, cfh);
    }

    public RocksDB getDatabase() {
        return database;
    }

    public RocksIterator getIter() {
        return getIter(TYPE_TRANSACTIONS);
    }

    public RocksIterator getIter(int columnFamily) {
        RocksIterator iterator = database.newIterator(cfh.get(columnFamily));
        iterator.seekToFirst();
        return iterator;
    }

    public RocksIterator getFirst(int columnFamily, byte[] _0) {
        RocksIterator iterator = getIter(columnFamily);
        if (_0 != null) {
            iterator.seek(_0);
        } else {
            iterator.seekToFirst();
        }
        return iterator;
    }

    public RocksIterator getLast(int columnFamily) {
        RocksIterator iterator = getIter(columnFamily);
        iterator.seekToLast();
        return iterator;
    }

    public byte[] get(int columnFamily, byte[] key) throws RocksDBException {
        return database.get(cfh.get(columnFamily), key);
    }

    public void put(int columnFamily, byte[] key, byte[] value) throws RocksDBException {
        database.put(cfh.get(columnFamily), key, value);
    }

    public void delete(byte[] key) throws RocksDBException {
        delete((byte) 0, key);
    }

    public void delete(int columnFamily, byte[] key) throws RocksDBException {
        database.delete(cfh.get(columnFamily), key);
    }

    public long getCount() { return getCount(RockDB.TYPE_TRANSACTIONS); }

    public long getCount(int columnFamily) {
        long count = 0;
        RocksIterator iterator = getIter(columnFamily);
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            count++;
        }
        return count;
    }

    public void close() {
        database.close();
    }
}
