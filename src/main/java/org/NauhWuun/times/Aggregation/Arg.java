package org.NauhWuun.times.Aggregation;

import java.util.Map;

import org.NauhWuun.times.Bytes;
import org.NauhWuun.times.TimeAxis;

public class Arg 
{
    public static String arg() {
        Map maps = TimeAxis.getMax();
        long key = Bytes.ConvertBytesToLong((byte[]) maps.entrySet().iterator().next());
        return TimeAxis.timeToHour(key / 2);
    }
}