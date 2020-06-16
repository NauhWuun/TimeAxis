package org.NauhWuun.times.Aggregation;

import org.NauhWuun.times.TimeAxis;

public class Max {
    public static String maxTime() {
        long key = TimeAxis.pollLastKey();
        return TimeAxis.timeToHour(key);
    }   
}