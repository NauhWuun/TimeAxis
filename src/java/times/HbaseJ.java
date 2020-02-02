package java.times;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.protobuf.generated.MasterProcedureProtos.DeleteColumnFamilyState;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.htrace.fasterxml.jackson.databind.Module.SetupContext;

public class HbaseJ 
{
    public static Connection connection;
    public static Admin admin;

    public static void setUp() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "master");
        conf.set("hbase.zookeeper.property.clientPort", "2020");

        connection = ConnectionFactory.createConnection(conf);
        admin = connection.getAdmin();
    }

    public static void createTable(String tableNameString) throws IOException {
        TableName tableName=TableName.valueOf(tableNameString);

        if (! admin.tableExists(tableName)) {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            HColumnDescriptor family = new HColumnDescriptor("base");

            hTableDescriptor.addFamily(family);
            admin.createTable(hTableDescriptor);
        }
    }

    public static void queryTable(String tableNameString) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableNameString));
        ResultScanner scanner = table.getScanner(new Scan());

        for (Result result : scanner) {
            byte[] row = result.getRow();
            List<Cell> listCells = result.listCells();

            for (Cell cell : listCells) {
                byte[] familyArray = cell.getFamilyArray();
                byte[] qualifierArray = cell.getQualifierArray();
                byte[] valueArray = cell.getValueArray();
            }
        }
    }

    public static void queryTableByRowKey(String tableNameString,String rowNameString) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableNameString));
        Get get = new Get(rowNameString.getBytes());

        Result result = table.get(get);
        byte[] row = result.getRow();

        List<Cell> listCells = result.listCells();

        for (Cell cell : listCells) {
            byte[] familyArray = cell.getFamilyArray();
            byte[] qualifierArray = cell.getQualifierArray();
            byte[] valueArray = cell.getValueArray();
        }
    }

    public static void deleteTable(String tableNameString) throws IOException {
        admin.disableTable(TableName.valueOf(tableNameString));
        admin.deleteTable(TableName.valueOf(tableNameString));
    }

    public static void addColumnFamily(String tableNameString, String columnFamily) throws IOException {
        TableName tableName = TableName.valueOf(tableNameString);
        HColumnDescriptor columnDescriptor = new HColumnDescriptor(columnFamily);
        admin.addColumn(tableName, columnDescriptor);
    }

    public static void DeleteColumnFamily(String tableNameString, String columnFamily) throws IOException {
        TableName tableName = TableName.valueOf(tableNameString);
        admin.deleteColumn(tableName, columnFamily.getBytes());
    }

    public static void insert(String tableNameString, List<Put> putList) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableNameString));
        table.put(putList);
    }   

    public static void shutDown() {
        admin.shutdown();
    }
}