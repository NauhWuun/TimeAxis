package org.NauhWuun.times;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class CountMinSketch
{
    private static final float DEFAULT_DELTA   = 0.01f;
    private static final float DEFAULT_EPSILON = 0.01f;

    private final int w;
    private final int d;

    private final int[][] multiset;

    public CountMinSketch() {
        this(DEFAULT_DELTA, DEFAULT_EPSILON);
    }

    public CountMinSketch(float delta, float epsilon) {
        this.w = (int) Math.ceil(Math.exp(1.0) / epsilon);
        this.d = (int) Math.ceil(Math.log(1.0 / delta));
        this.multiset = new int[d][w];
    }

    public CountMinSketch(int width, int depth) {
        this.w = width;
        this.d = depth;
        this.multiset = new int[d][w];
    }

    private CountMinSketch(int width, int depth, int[][] ms) {
        this.w = width;
        this.d = depth;
        this.multiset = ms;
    }

    public int getWidth() {
        return w;
    }

    public int getDepth() {
        return d;
    }

    public long getSizeInBytes() {
        return ((w * d) + 2) * (Integer.SIZE / 8);
    }

    public void set(byte[] key) {
        long hash64 = HashAlgorithm.FNV1A_64_HASH(Arrays.toString(key));
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);

        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);

            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }

            int pos = combinedHash % w;
            multiset[i - 1][pos] += 1;
        }
    }

    public void setString(String val) {
        set(val.getBytes());
    }

    public void setByte(byte val) {
        set(new byte[]{val});
    }

    public void setInt(int val) {
        set(intToByteArrayLE(val));
    }

    public void setLong(long val) {
        set(longToByteArrayLE(val));
    }

    public void setFloat(float val) {
        setInt(Float.floatToIntBits(val));
    }

    public void setDouble(double val) {
        setLong(Double.doubleToLongBits(val));
    }

    private static byte[] intToByteArrayLE(int val) {
        return new byte[]{(byte) (val),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24)};
    }

    private static byte[] longToByteArrayLE(long val) {
        return new byte[]{(byte) (val),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24),
                (byte) (val >> 32),
                (byte) (val >> 40),
                (byte) (val >> 48),
                (byte) (val >> 56),};
    }

    public int getEstimatedCount(byte[] key) {
        long hash64 = HashAlgorithm.FNV1A_64_HASH(Arrays.toString(key));

        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);

        int min = Integer.MAX_VALUE;

        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }

            int pos = combinedHash % w;
            min = Math.min(min, multiset[i - 1][pos]);
        }

        return min;
    }

    public int getEstimatedCountString(String val) {
        return getEstimatedCount(val.getBytes());
    }

    public int getEstimatedCountByte(byte val) {
        return getEstimatedCount(new byte[]{val});
    }

    public int getEstimatedCountInt(int val) {
        return getEstimatedCount(intToByteArrayLE(val));
    }

    public int getEstimatedCountLong(long val) {
        return getEstimatedCount(longToByteArrayLE(val));
    }

    public int getEstimatedCountFloat(float val) {
        return getEstimatedCountInt(Float.floatToIntBits(val));
    }

    public int getEstimatedCountDouble(double val) {
        return getEstimatedCountLong(Double.doubleToLongBits(val));
    }

    public void merge(CountMinSketch that) {
        if (that == null) {
            return;
        }

        if (this.w != that.w) {
            throw new RuntimeException("Merge failed! Width of count min sketch do not match!" +
                    "this.width: " + this.getWidth() + " that.width: " + that.getWidth());
        }

        if (this.d != that.d) {
            throw new RuntimeException("Merge failed! Depth of count min sketch do not match!" +
                    "this.depth: " + this.getDepth() + " that.depth: " + that.getDepth());
        }

        for (int i = 0; i < d; i++) {
            for (int j = 0; j < w; j++) {
                this.multiset[i][j] += that.multiset[i][j];
            }
        }
    }

    public static byte[] serialize(CountMinSketch cms) {
        long serializedSize = cms.getSizeInBytes();

        ByteBuffer bb = ByteBuffer.allocate((int) serializedSize);
        bb.putInt(cms.getWidth());
        bb.putInt(cms.getDepth());

        for (int i = 0; i < cms.getDepth(); i++) {
            for (int j = 0; j < cms.getWidth(); j++) {
                bb.putInt(cms.multiset[i][j]);
            }
        }

        bb.flip();
        return bb.array();
    }

    public static CountMinSketch deserialize(byte[] serialized) {
        ByteBuffer bb = ByteBuffer.allocate(serialized.length);

        bb.put(serialized);
        bb.flip();

        int width = bb.getInt();
        int depth = bb.getInt();

        int[][] multiset = new int[depth][width];

        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < width; j++) {
                multiset[i][j] = bb.getInt();
            }
        }

        return new CountMinSketch(width, depth, multiset);
    }
}