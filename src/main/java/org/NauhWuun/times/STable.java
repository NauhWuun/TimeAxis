package org.NauhWuun.times;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class STable
{
	private STable() {}

	public static STable createInstance() { return new STable(); }

	public void merge(ConcurrentSkipListMap<KEY, VALUE> obj) {
		HashMap<KEY, VALUE> c2 = new HashMap<>();
		c2.putAll(obj);
		
		byte[] bytes = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(c2);

			bytes = os.toByteArray();
			oos.close();
			os.close();

			Block tableBlock = new Block(bytes);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}