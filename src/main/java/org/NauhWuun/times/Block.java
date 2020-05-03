package org.NauhWuun.times;

import java.util.Arrays;

public final class Block
{
    private static final long FNV_64_INIT  = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private final byte[] hash;
    private final byte[] data;
    private final byte[] encoded;

    public Block(final byte[] data) {
        this.data = data;

        Encoder enc = new Encoder();
        enc.writeBytes(data);
        this.encoded = enc.toBytes();
        this.hash = Bytes.convertToByteArray(HashAlgorithm.FNV1A_64_HASH(Arrays.toString(encoded)));
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