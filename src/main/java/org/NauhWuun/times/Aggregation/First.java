package org.NauhWuun.times.Aggregation;

import java.util.Map;
import org.NauhWuun.times.TimeAxis;

public class First 
{
    public static Map<Object, Object> first() {
        return TimeAxis.poll(30);
    }    
}