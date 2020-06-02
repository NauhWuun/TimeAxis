package org.NauhWuun.times;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.rocksdb.RocksDBException;

public final class STable
{
	private STable() {}

	public static STable createInstance() { return new STable(); }

	public void merge(ConcurrentSkipListMap<KEY, VALUE> obj) {
		HashMap<KEY, VALUE> c2 = new HashMap<>(obj);
		
		byte[] bytes = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(c2);

			bytes = os.toByteArray();
			oos.close();
			os.close();

			Block tableBlock = new Block(bytes);
			TimeAxis.db.put(Bytes.convertToByteArray(System.currentTimeMillis()), tableBlock.toBytes());
		} catch (IOException | RocksDBException e) {
			e.printStackTrace();
		}
	}
}