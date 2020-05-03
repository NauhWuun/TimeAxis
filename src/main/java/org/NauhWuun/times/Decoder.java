package org.NauhWuun.times;

public final class Decoder
{
    private static final String ENCODING = "UTF-8";

    private final byte[] in;
    private final int from;
    private final int to;

    private int index;

    public Decoder(byte[] in) {
        this(in, 0, in.length);
    }

    public Decoder(byte[] in, int from) {
        this(in, from, in.length);
    }

    public Decoder(byte[] in, int from, int to) {
        this.in = in;
        this.from = from;
        this.to = to;
        this.index = from;
    }

    public int readInt() {
        require(4);
        return in[index++] << 24 | (in[index++] & 0xFF) << 16 | (in[index++] & 0xFF) << 8 | (in[index++] & 0xFF);
    }

    public byte[] readBytes(boolean vlq) {
        int len = vlq ? readSize() : readInt();

        require(len);
        byte[] buf = new byte[len];
        System.arraycopy(in, index, buf, 0, len);
        index += len;

        return buf;
    }

    public byte[] readBytes() {
        return readBytes(true);
    }

    protected int readSize() {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            require(1);
            byte b = in[index++];

            size = (size << 7) | (b & 0x7F);
            if ((b & 0x80) == 0) {
                break;
            }
        }
        return size;
    }

    protected void require(int n) {
        if (to - index < n) {
            String msg = String.format("input [%d, %d], require: [%d %d]", from, to, index, index + n);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    protected long unsignedInt(int i) {
        return i & 0x00000000ffffffffL;
    }
}
