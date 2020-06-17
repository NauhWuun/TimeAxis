package org.NauhWuun.times;

import org.rocksdb.RocksDBException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class Reduce
{
	private Reduce() {}

	public synchronized static void merge(byte[] times, Map<KEY, VALUE> obj) {
		System.out.println("meger");
		ObjectOutputStream oos = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			byte[] bytes = os.toByteArray();
			Block tableBlock = new Block(bytes);
			TimeAxis.db.put(RockDB.TYPE_TRANSACTIONS, times, tableBlock.toBytes());
		} catch (IOException | RocksDBException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println("meger finish");
		}
	}

	public synchronized static Map<KEY, VALUE> divergence(byte[] data) {
		ByteArrayInputStream os = null;
		ObjectInputStream stream = null;
		try {
			byte[] bytes = TimeAxis.db.get(RockDB.TYPE_TRANSACTIONS, data);
			Block block = Block.fromBytes(bytes);
			os = new ByteArrayInputStream(block.getData());
			stream = new ObjectInputStream(os);
			return (Map<KEY, VALUE>) stream.readObject();
		} catch (IOException | ClassNotFoundException | RocksDBException e) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}