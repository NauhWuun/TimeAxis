package org.NauhWuun.times;

import java.util.Arrays;

public final class Block
{
    private final byte[] hash;
    private final byte[] data;
    private final byte[] encoded;

    private String timeStamp;

    public Block(final byte[] data) {
        this.data = data;

        Encoder enc = new Encoder();
        enc.writeBytes(data);
        this.encoded = enc.toBytes();
        this.hash = Bytes.convertToByteArray(HashAlgorithm.FNV1A_64_HASH(Arrays.toString(encoded)));
        this.timeStamp = String.valueOf(System.currentTimeMillis());
    }

    public Block(final byte[] hash, final byte[] encoded) {
        this.hash = hash;

        Decoder dec = new Decoder(encoded);
        this.data = dec.readBytes();
        this.encoded = encoded;
    }

    public byte[] getHash() {
        return hash;
    }
    public byte[] getData() {
        return data;
    }

    public String getTimeStamp() {
        return timeStamp;
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
}