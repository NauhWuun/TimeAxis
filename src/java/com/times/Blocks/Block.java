package com.times.Blocks;

import com.times.Codec.Decoder;
import com.times.Codec.Encoder;

public class Block
{
    private final byte[] hash;
    private final String name;
    private final long timestamp;
    private final byte[] data;
    private final byte[] encoded;

    private static final long FNV_64_INIT  = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    public Block(String name, long timestamp, byte[] data) {
        this.name = name;
        this.timestamp = timestamp;
        this.data = data;

        Encoder enc = new Encoder();
        enc.writeString(name);
        enc.writeLong(timestamp);
        enc.writeBytes(data);
        this.encoded = enc.toBytes();
        this.hash = FNV1A_64_HASH(new String(encoded)).getBytes();
    }

    public Block(byte[] hash, byte[] encoded) {
        this.hash = hash;

        Decoder dec = new Decoder(encoded);
        this.name = dec.readString();
        this.timestamp = dec.readLong();
        this.data = dec.readBytes();

        this.encoded = encoded;
    }

    public byte[] getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getData() {
        return data;
    }

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
        return "Block [name=" + name + ", timestamp=" + timestamp + ", data=" +  (new String(data).toString()) + ", hash=" + (new String(hash).toString()) + "]";
    }
}