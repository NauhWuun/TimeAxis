package org.NauhWuun.times.RowCols;

import org.NauhWuun.times.Blocks.Block;
import org.NauhWuun.times.Until.Pair;
import org.NauhWuun.times.Until.RockRand;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Date;
import java.util.TimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class RowColumn
{
    private final String describe, name;
    private long initTimeStamp;
    private long id;

    private SortedMap<Object, Object> tags;
    private Set<Map.Entry<Object, Object>> sortEntry;
    private Iterator<Map.Entry<Object, Object>> iter;

    public RowColumn(final String describe, final String name) {
        this.describe = describe;
        this.name = name;
        this.initTimeStamp = date2Stamp(createdateTime());
        this.id = RockRand.getUnsignedLong();

        tags = new TreeMap<Object, Object>();

        sortEntry = tags.entrySet();
        iter = sortEntry.iterator();
    }

    public Pair<Long, Boolean> containsTag(Object tag) {
        return new Pair<>(date2Stamp(createdateTime()), tags.containsKey(tag));
    }

    public Iterator<Map.Entry<Object, Object>> getColumnIterator() {
        return iter;
    }

    public Pair<Long, Long> put(final String tag, Object value) {
        put(tag, value);
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, Long> put(final String tag, Object... values) {
        for (Object v : values) {
            tags.put(tag, v);
        }
        
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, byte[]> ColdDataBlocking(String timeData) {
        return new Pair<>(date2Stamp(createdateTime()), new Block(timeData).toBytes());
    }

    public Pair<Long, Block> hotBlockingData(byte[] hotdata) {
        return new Pair<>(date2Stamp(createdateTime()), Block.fromBytes(hotdata));
    }

    @Deprecated
    public Pair<Long, Void> flushData() {
        return new Pair(date2Stamp(createdateTime()), Void.TYPE);
    }

    public Pair<Long, Long> remove(final String tag) {
        tags.remove(tag);
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, Map<Object, Object>> of(final String tag, Object value) {
        return new Pair<>(date2Stamp(createdateTime()), Map.of(tag, value));
    }

    public Pair<Long, Boolean> Replace(final String tag, Object oldValue, Object newValue) {
        return new Pair<>(date2Stamp(createdateTime()),
                tags.replace(tag, oldValue, newValue));
    }

    public Pair<Long, Set<Object>> KeySet() {
        return new Pair<>(date2Stamp(createdateTime()), tags.keySet());
    }

    public Pair<Long, Collection<Object>> Values() {
       return new Pair<>(date2Stamp(createdateTime()), tags.values());
    }

    public Pair<Long, Object> getTag(String tag) {
        return new Pair<>(date2Stamp(createdateTime()), tags.get(tag));
    }

    public Pair<Long, Long> Clear() {
    //    tags.clear();
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, Long> PutAll(Map<? extends Object, ? extends Object> m) {
        tags.putAll(m);
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, Object> FirstKey() {
        return new Pair<>(date2Stamp(createdateTime()), tags.get(iter.next()));
    }

    public Pair<Long, Object> LastKey() {        
        return new Pair<>(date2Stamp(createdateTime()), tags.lastKey());
    }

    public Pair<Long, Boolean> IsEmpty() {
        return new Pair<>(date2Stamp(createdateTime()), tags.isEmpty());
    }

    public Pair<Long, Integer> Size() {
        return new Pair<>(date2Stamp(createdateTime()), tags.size());
    }

    public Pair<Long, String> getRowColumnDescribe() {
        return new Pair<>(date2Stamp(createdateTime()), describe);
    }

    public Pair<Long, String> getRowColumnName() {
        return new Pair<>(date2Stamp(createdateTime()), name);
    }

    public Pair<Long, Long> getCreateTimestamp() {
        return new Pair<>(date2Stamp(createdateTime()), initTimeStamp);
    }

    public Pair<Long, String> getFormatTimeStamp() {
        return new Pair<>(date2Stamp(createdateTime()), stamp2Date(initTimeStamp));
    }

    public Long getCurrentTimestamp() {
        return date2Stamp(createdateTime());
    }

    public final long getRowColumnID() { return id; }

    public static String createdateTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z").format(new Date());
    }

    private static long date2Stamp(final String strings) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z").parse(strings).getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
	}

    private static String stamp2Date(final long stamp) {
		Date date = new Date(stamp * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		return sdf.format(date);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        while (iter.hasNext()) {
            sb.append("DateTime= " + getFormatTimeStamp().getRight() + " Column= " + getRowColumnName()  + " describe=" + getRowColumnDescribe().getRight() +
                " name= " + getRowColumnName().getRight() +" Tag= " + iter.next().getKey() + " value= " + iter.next().getValue() + "\r\n");
        }

        return sb.toString();
    }
}