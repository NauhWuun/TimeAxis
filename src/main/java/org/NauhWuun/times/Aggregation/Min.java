package org.NauhWuun.times.Aggregation;

import org.NauhWuun.times.TimeAxis;

public class Min 
{
    public static String minTime() {
        long key = TimeAxis.pollFirstKey();
        return TimeAxis.timeToHour(key);
    }
}