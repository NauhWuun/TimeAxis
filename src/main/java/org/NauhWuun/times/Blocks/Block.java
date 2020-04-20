package org.NauhWuun.times.Blocks;

import org.NauhWuun.times.Codec.Decoder;
import org.NauhWuun.times.Codec.Encoder;
import org.NauhWuun.times.RowCols.Rows;

import java.util.Arrays;

public class Block
{
    private static final long FNV_64_INIT  = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private final byte[] hash;
    private final String timeStamp;
    private final byte[] data;
    private final byte[] encoded;

    public Block(final byte[] data) {
        this.data = data;
        this.timeStamp = Rows.createdateTime();

        Encoder enc = new Encoder();
        enc.writeString(timeStamp);
        enc.writeBytes(data);
        this.encoded = enc.toBytes();
        this.hash = FNV1A_64_HASH(new String(encoded)).getBytes();
    }

    public Block(final byte[] hash, final byte[] encoded) {
        this.hash = hash;

        Decoder dec = new Decoder(encoded);
        this.timeStamp = dec.readString();
        this.data = dec.readBytes();
        this.encoded = encoded;
    }

    public byte[] getHash() {
        return hash;
    }
    public byte[] getData() {
        return data;
    }
    public String getTimeStamp() { return timeStamp; }

    public byte[] toBytes() {
        Encoder enc = new Encoder();
        enc.writeBytes(hash);
        enc.writeBytes(encoded);
        return enc.toBytes();
    }

    public static Block fromBytes(byte[] bytes) {
        Decoder dec = new Decoder(bytes);
        byte[] hash = dec.readBytes();
        byte[] encoded = dec.readBytes();

        return new Block(hash, encoded);
    }

    public static String FNV1A_64_HASH(final String k) {
        long rv = FNV_64_INIT;
        int len = k.length();

        for (int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_64_PRIME;
        }

        return String.valueOf(rv);
    }

    @Override
    public String toString() {
        return "Block [TimeStamp= " + getTimeStamp() + "\t data= " + Arrays.toString(data) + "\t hash= " + Arrays.toString(hash) + "]";
    }
}