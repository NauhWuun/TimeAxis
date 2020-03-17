package org.NauhWuun.times.DataBase;

import org.rocksdb.RocksDB;

public final class LocalDB
{
    static {
        RocksDB.loadLibrary();
    }

    private RocksDB database;
    private DBOptions options;

    public LocalDB(String db) throws RocksDBException {
        File file = new File(db);
        if (! file.exists()) {
            file.mkdirs();
        }

        options = new DBOptions()
                      .setCreateMissingColumnFamilies(true)
                      .setCreateIfMissing(true)
                      .setParanoidChecks(true)
                      .setMaxOpenFiles(8192);

        database = RocksDB.open(options, db, cfd, cfh);
    }

    public RocksDB getDatabase() {
        return database;
    }

    public RocksIterator getIter() {
        return getIter(0);
    }

    public RocksIterator getFirst(byte[] _0) {
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
        delete((byte) 0, key);
    }

    public void close() {
        database.close();
    }
}
