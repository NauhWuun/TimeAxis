package java.times;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class TimeAxis
{
    private int MAX_FREEZING_TIMES_HOURS = 24;

    private Map<String, InternalAxis> maps;

    public TimeAxis() {
        maps = new HashMap<String, InternalAxis>();
    }

    public TimeAxis Builder() {
        return this;
    }

    public TimeAxis createRowColumn(final String describe, final String name) {
        RowColumn rc = new RowColumn(describe, name);
        InternalAxis ia = new InternalAxis(name, rc);

        maps.put(name, ia);
        return this;
    }

    public TimeAxis addTagValue(final String name, final String tag, Object value) {
        maps.get(name).getRowColumn().put(tag, value);
        return this;
    }

    public void Finish() {
        return;
    }

    public static HashMap<String, InternalAxis> InvertedMap(Map<String, InternalAxis> invertTheMap) {
		Set<Entry<String, InternalAxis>> set = invertTheMap.entrySet();
		ArrayList<Entry<String, InternalAxis>> arrayList = new ArrayList<>(set);
 
		Collections.sort(arrayList, new Comparator<Entry<String, InternalAxis>>() {
            @Override
			public int compare(Entry<String, InternalAxis> arg0, Entry<String, InternalAxis> arg1) {
				return (arg1.getValue().getRowColumn().getCurrentTimestamp().compare(arg0.getValue().getRowColumn().getCurrentTimestamp()));
			}
		});
		
		LinkedHashMap<String, InternalAxis> map = new LinkedHashMap<>();
 
		for (int i = 0; i < arrayList.size(); i++) {
			Entry<String, InternalAxis> entry = arrayList.get(i);
			map.put(entry.getKey(), entry.getValue());
		}
 
		return map;
    }

    public void toLocalDisk() {
        // store data to local disk.
        HbaseJ.setUp();
        HbaseJ.createTable(name);
        HBaseJ.shutDown();
    }

    public void loadDisktoMemory() {
        // loading local data to memory
    }
    
    /**
     * Auto freezing more than 24 hours datas

     */
    private void autoFreezing() {
        new Thread(()-> 
        {
            //
            // scanning all rowColumns times compare current times.
            // Data will be recycled after 24 hours
            //
            // @See current >= MAX_FREEZING_TIMES_HOURS
            //
            // @apiNote Used blocking policy packing to Disk changed the cold datas, saving memory
            //
            HashMap<String, InternalAxis> freezMaps = TimeAxis.InvertedMap(maps);
		}).start();
    }

    @Override
    public String toString() {
        return "Time-Axis DataBase";
    }

    class InternalAxis
    {
        private String colName;
        private RowColumn rowColumn;
        private long timestamp;

        public InternalAxis(final String colName, RowColumn rowColumn) {
            this.colName = colName;
            this.rowColumn = rowColumn;
            this.rowColumn.getCreateTimestamp().getRight();
        }

		public RowColumn getRowColumn() {
            return this.rowColumn;
        }

        public String getcolName() {
            return this.colName;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }
}