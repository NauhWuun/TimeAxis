package org.NauhWuun.times.Blocks;

import org.NauhWuun.times.Codec.Decoder;
import org.NauhWuun.times.Codec.Encoder;
import org.NauhWuun.times.RowCols.RowColumn;

public class Block
{
    private static final long FNV_64_INIT  = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private final byte[] hash;
    private long timeStamp;
    private final String data;
    private final byte[] encoded;

    public Block(String data) {
        this.data = data;
        this.timeStamp = Long.parseLong(RowColumn.createdateTime());

        Encoder enc = new Encoder();
        enc.writeLong(timeStamp);
        enc.writeString(data);
        this.encoded = enc.toBytes();
        this.hash = FNV1A_64_HASH(new String(encoded)).getBytes();
    }

    public Block(byte[] hash, byte[] encoded) {
        this.hash = hash;

        Decoder dec = new Decoder(encoded);
        this.timeStamp = dec.readLong();
        this.data = dec.readString();

        this.encoded = encoded;
    }

    public byte[] getHash() {
        return hash;
    }
    public String getData() {
        return data;
    }
    public Long getTimeStamp() { return timeStamp; }

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

    public static final String FNV1A_64_HASH(final String k) {
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
        return "Block [TimeStamp= " + getTimeStamp() + "\t data= " +  (new String(data).toString()) + "\t hash= " + (new String(hash).toString()) + "]";
    }
}