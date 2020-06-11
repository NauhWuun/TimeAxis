package org.NauhWuun.times.Aggregation;

import java.util.Map;

import org.NauhWuun.times.TimeAxis;

public class Last 
{
    public static Map<Object, Object> last() {
        return TimeAxis.pollLast();
    }    
}