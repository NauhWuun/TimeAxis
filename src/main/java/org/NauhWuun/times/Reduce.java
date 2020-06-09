package org.NauhWuun.times;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.rocksdb.RocksDBException;

public final class Reduce
{
	private Reduce() {}

	public synchronized static void merge(ConcurrentSkipListMap<KEY, VALUE> obj) {
		HashMap<KEY, VALUE> c2 = new HashMap<>(obj);
		
		byte[] bytes = null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(c2);

			bytes = os.toByteArray();
			oos.close();
			os.close();

			Block tableBlock = new Block(bytes);
			TimeAxis.db.put(RockDB.TYPE_TRANSACTIONS, Bytes.convertToByteArray(System.currentTimeMillis()), tableBlock.toBytes());
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
		}
	}

	public synchronized static Map<Object, Object> divergence(byte[] data) {
		ByteArrayInputStream os = null;
		ObjectInputStream stream = null;
		byte[] bytes = null;

        try {
			bytes = TimeAxis.db.get(RockDB.TYPE_TRANSACTIONS, data);
			Block block = Block.fromBytes(bytes);
			os = new ByteArrayInputStream(block.getData());
            stream = new ObjectInputStream(os);
			return (Map<Object, Object>) stream.readObject();
        } catch (IOException | ClassNotFoundException | RocksDBException e) {
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
	}
}