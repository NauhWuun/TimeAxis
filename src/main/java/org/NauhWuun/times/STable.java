package org.NauhWuun.times;

import org.rocksdb.RocksDBException;

import java.util.Map;
import java.util.TreeMap;

public final class STable
{
	private TreeMap<KEY, VALUE> c2;

	public STable() {
		c2 = new TreeMap<>();
	}

	public void merge(Object obj) {
		TreeMap<KEY, VALUE> c02 = (TreeMap<KEY, VALUE>) obj;
		for (Map.Entry<KEY, VALUE> v : c02.entrySet()) {
			c2.put(v.getKey(), v.getValue());
		}

		StringBuilder sb = new StringBuilder();
		c2.entrySet().parallelStream().forEach((key) -> {
			sb.append(key.getKey().getTime());
			sb.append(key.getValue().getValue());
		});

		final Block timeBlock = new Block(sb.toString().getBytes());
		try {
			TimeAxis.getDBInstance().put(timeBlock.getHash(), timeBlock.toBytes());
		} catch (RocksDBException e) {
			e.printStackTrace();
		}
	}
}