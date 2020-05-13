package org.NauhWuun.times;

import org.rocksdb.RocksDBException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentSkipListMap;

public final class STable
{
	private STable() {}

	public static STable createInstance() { return new STable(); }

	public void merge(ConcurrentSkipListMap<byte[], byte[]> obj) {
		ConcurrentSkipListMap<byte[], byte[]> c2 = obj;
		
		byte[] bytes = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(c2);

			bytes = os.toByteArray();
			oos.close();
			os.close();

			Block tableBlock = new Block(bytes);
			TimeAxis.getDBInstance().put(RockDB.TIMES, tableBlock.getTimeStamp().getBytes(), tableBlock.toBytes());
		} catch (RocksDBException | IOException e) {
			e.printStackTrace();
		}
	}
}