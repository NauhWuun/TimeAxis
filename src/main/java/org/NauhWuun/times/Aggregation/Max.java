package org.NauhWuun.times.Aggregation;

import java.util.Map;

import org.NauhWuun.times.TimeAxis;

public class Max {
    public static Map<Object, Object> maxTimeData() {
        try {
            return TimeAxis.getMax();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }   
}