package org.NauhWuun.times.Aggregation;

import java.util.Map;

import org.NauhWuun.times.KEY;
import org.NauhWuun.times.TimeAxis;
import org.NauhWuun.times.VALUE;

public class First 
{
    public static Map<KEY, VALUE> first() {
        return TimeAxis.poll(30);
    }    
}