package org.NauhWuun.times;

import java.io.Serializable;

public final class VALUE implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -4751811816938874135L;

    byte[] param;

    private VALUE(String params) { param = Bytes.convertToByteArray(params); }
    public static VALUE Builder(String value) { return new VALUE(value); }
    public final String getValue() { return Bytes.ConvertBytesToString(param); }
}