package org.NauhWuun.times;

public final class VALUE
{
    byte[] param;

    public VALUE() {
        this("0");
    }

    public VALUE(final String params) {
        param = Bytes.convertToByteArray(params);
    }

    public final String getValue() {
        return Bytes.ConvertBytesToString(param);
    }
}