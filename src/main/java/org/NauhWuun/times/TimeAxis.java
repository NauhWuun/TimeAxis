package org.NauhWuun.times;

public final class TimeAxis 
{
    private static final STree maps = new STree();

    public void push(String key, String value) {
        maps.add(KEY.Builder(key), VALUE.Builder(value));
    }

    public void Remove(String key) {
        maps.delete(KEY.Builder(key));
    }

    public String get(String key) {
        return maps.get(KEY.Builder(key)).getValue();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase \r\n";
    }
}