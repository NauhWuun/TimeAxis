package org.NauhWuun.times;

public final class VALUE
{
    byte[] param;

    private VALUE(String params) {
        param = Bytes.convertToByteArray(params);
    }

    public static VALUE Builder(String value) { return new VALUE(value); }
    public final String getValue() { return Bytes.ConvertBytesToString(param); }
}