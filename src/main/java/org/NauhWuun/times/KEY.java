package org.NauhWuun.times;

public final class KEY
{
    long dateTime;
    String key;

    private KEY(String key) { 
        this.setCreatedTime(); 
        this.key = key;
    }

    public static KEY Builder(String key) { return new KEY(key); }

    private void setCreatedTime() { dateTime = System.currentTimeMillis(); }

    public final long getTime() {
        return dateTime;
    }

    public Boolean validator() {
        return dateTime > 0;
    }

    public String getKey() { return key; }
}