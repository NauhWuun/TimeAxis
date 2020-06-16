package org.NauhWuun.times.Aggregation;

import org.NauhWuun.times.TimeAxis;

public class Arg 
{
    public static String arg() throws IllegalArgumentException {
        long key = TimeAxis.pollLastKey();
        return TimeAxis.timeToHour(key / 2);
    }
}