package org.NauhWuun.times.Until;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.KryoDataInput;
import com.esotericsoftware.kryo.io.KryoDataOutput;
import com.esotericsoftware.kryo.io.Output;
import org.roaringbitmap.RoaringBitmap;

import java.io.IOException;

public class RoaringSerializer extends Serializer<RoaringBitmap>
{
	@Override
	public void write(Kryo kryo, Output output, RoaringBitmap integers) {
		try {
			integers.serialize(new KryoDataOutput(output));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public RoaringBitmap read(Kryo kryo, Input input, Class<? extends RoaringBitmap> aClass) {
		RoaringBitmap bitmap = new RoaringBitmap();
		try {
			bitmap.deserialize(new KryoDataInput(input));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return bitmap;
	}
}
