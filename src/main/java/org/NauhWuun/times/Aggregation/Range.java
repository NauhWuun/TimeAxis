package org.NauhWuun.times.Aggregation;

import java.util.HashMap;
import java.util.Map;

import org.NauhWuun.times.TimeAxis;

public class Range 
{
    /**
     *
     * @param start times: current times/second
              end times: endof times/second
              per times: a unit of 30/s
     * @return start to end times all dat int maps
     */
    public static Map<Object, Object> poll(long start, long end) {
        Map<Object, Object> newMaps = new HashMap<>();
        for (; start < end; start += 30) {
            newMaps.putAll(TimeAxis.poll(start));
        }
        return newMaps;
    }
}