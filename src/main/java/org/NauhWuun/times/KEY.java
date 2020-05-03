package org.NauhWuun.times;

public final class KEY
{
    long dateTime;

    public KEY() { this.setCreatedTime(); }

    private void setCreatedTime() { dateTime = System.currentTimeMillis(); }

    public final long getTime() {
        return dateTime;
    }

    public Boolean validator() {
        return dateTime > 0;
    }
}