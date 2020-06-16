package org.NauhWuun.TimeAxis;

import org.NauhWuun.times.TimeAxis;

public class Test 
{
    public static void main(String[] args) throws InterruptedException {
        TimeAxis timeAxis = new TimeAxis();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            timeAxis.push(i + "key", i + "value");
            System.out.println(i);
        }
    }    
}