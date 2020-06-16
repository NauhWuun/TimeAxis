package org.NauhWuun.times.Aggregation;

import java.util.Map;

import org.NauhWuun.times.KEY;
import org.NauhWuun.times.TimeAxis;
import org.NauhWuun.times.VALUE;

public class Last 
{
    public static Map<KEY, VALUE> last() {
        return TimeAxis.pollLast();
    }    
}