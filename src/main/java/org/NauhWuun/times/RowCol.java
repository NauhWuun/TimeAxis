package org.NauhWuun.times;

import org.NauhWuun.times.Until.Pair;
import org.NauhWuun.times.Until.RockRand;

import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class RowCol
{
    private final long id;
    private final long initTimeStamp;

    private final SortedMap<Object, Object> tags;

    public RowCol() {
        this.initTimeStamp = date2Stamp(createdateTime());
        this.id = RockRand.getUnsignedLong();

        tags = new TreeMap<>();
    }

    public Pair<Long, Boolean> containsTag(Object tag) {
        return new Pair<>(date2Stamp(createdateTime()), tags.containsKey(tag));
    }

    public void Put(final String tag, Object value) {
        tags.put(tag, value);
    }

    public Pair<Long, Long> remove(final String tag) {
        tags.remove(tag);
        return new Pair<>(date2Stamp(createdateTime()), date2Stamp(createdateTime()));
    }

    public Pair<Long, Map<Object, Object>> of(final String tag, Object value) {
        return new Pair<>(date2Stamp(createdateTime()), Map.of(tag, value));
    }

    public Pair<Long, Boolean> Replace(final String tag, Object oldValue, Object newValue) {
        return new Pair<>(date2Stamp(createdateTime()), tags.replace(tag, oldValue, newValue));
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

    public Pair<Long, Long> Clear() { return new Pair<>(0L, 0L); }

    public void PutAll(Map<?, ?> m) { tags.putAll(m); }

    public Pair<Long, Object> FirstKey() {
        return new Pair<>(date2Stamp(createdateTime()), tags.firstKey());
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

    public Pair<Long, Long> getCreateTimestamp() {
        return new Pair<>(date2Stamp(createdateTime()), initTimeStamp);
    }

    public Pair<Long, String> getFormatTimeStamp() {
        return new Pair<>(date2Stamp(createdateTime()), stamp2Date(initTimeStamp));
    }

    public Long getCurrentTimestamp() {
        return date2Stamp(createdateTime());
    }

    public final Long getRowColumnID() { return id; }

    public Map<Object, Object> getKeyValues() {
        Map<Object, Object> maps = new HashMap<>(tags.size());
        maps.putAll(tags);
        return maps;
    }

    public static String createdateTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z").format(new Date());
    }

    public static String getTimeStampSubType(String strings, String dateType) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");
        Date date = format.parse(strings);
        SimpleDateFormat df = new SimpleDateFormat(dateType);
        return df.format(date);
    }

    private long date2Stamp(final String strings) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z").parse(strings).getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
	}

    private String stamp2Date(final long stamp) {
		Date date = new Date(stamp * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		return sdf.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (! (o instanceof RowCol)) return false;

        RowCol rc = (RowCol) o;
        return id == rc.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}