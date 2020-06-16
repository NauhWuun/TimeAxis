package org.NauhWuun.TimeAxis;

import jdk.jfr.Description;
import org.NauhWuun.times.Aggregation.*;
import org.NauhWuun.times.TimeAxis;

public class Test 
{
    @Description("Push data")
    public static void Push() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            new TimeAxis().push(i + "key", i + "value");
        }
    }

    @Description("Poll Data")
    public static void Poll() {
        System.out.println("Arg Time Value: " + Arg.arg());
        System.out.println("Count Value: " + Count.count());
        System.out.println("First Values: " + First.first().entrySet().iterator().next().toString());
        System.out.println("Last Values: " + Last.last().entrySet().iterator().next().toString());
        System.out.println("Max Time Value: " + Max.maxTime());
        System.out.println("Min Time Value: " + Min.minTime());
        Range.poll(30, 240).forEach((k, v) -> System.out.println("K/V Values: " + k.toString() + " " + v.toString()));
        System.out.println("Sum Times Value: " + Sum.sumTime());
    }

    @Description("main set")
    public static void main(String[] args) {

    }    
}