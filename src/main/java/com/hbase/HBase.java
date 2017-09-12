package com.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;  
  
public class HBase {  

    /** 
     * @param args 
     * @throws ZooKeeperConnectionException  
     * @throws MasterNotRunningException  
     */  
    public static void main(String[] args) throws Exception {  
        String tableName = "user_visit";
        // 第一步：创建数据库表：“student”
        String[] columnFamilys = { "visit" };
        //create 'student','info','course'
//        deleteTable(tableName);
//        getAllRows(tableName);

//        createTable(tableName, columnFamilys);
        // 第二步：向数据表的添加数据
        if (isExist(tableName)) {
        	// 添加第一行数据
        	//表名，行，列族，列，value
        	//put 'student','zpc','info:age','20'
//            addRow(tableName, "15058196972", "visit", "car_serial", "20");
//            addRow(tableName, "15058196972", "visit", "car_type", "20");
//            addRow(tableName, "15058196972", "visit", "car_serial", "56");

            //get 'student','zpc'
//            getRow(tableName, "1519225222220170808131841");
//        	getAllRowsByRowRange(tableName, "1300160600020170808000000", "1300160600020170809000000");
//        	getAllRowsByTimeRange(tableName, 0L, 2502168879995L);

            // 第四步：获取所有数据
            System.out.println("**************获取所有数据***************");
            //scan 'student'
            getAllRows(tableName);

//            // 第五步：删除一行数据
//            System.out.println("************删除一条(zpc)数据************");
//            //deleteall 'student','zpc'
//            delRow(tableName, "zpc");
//            getAllRows(tableName);
//            // 第六步：删除多条数据
//            System.out.println("**************删除多条数据***************");
//            String rows[] = new String[] { "qingqing","xiaoxue" };
//            delMultiRows(tableName, rows);
//            getAllRows(tableName);
//            // 第七步：删除数据库
//            System.out.println("***************删除数据库表**************");
////            deleteTable(tableName);
//            System.out.println("表"+tableName+"存在吗？"+isExist(tableName));
        } else {
            System.out.println(tableName + "此数据库表不存在！");
        }       
    }
    
    // 创建数据库表
    public static void createTable(String tableName, String[] columnFamilys)
            throws Exception {
        // 新建一个数据库管理员
        TableName table = TableName.valueOf(tableName);
        if (HBaseClient.getHBaseAdmin().tableExists(table)) {
            System.out.println("表 "+tableName+" 已存在！");
            System.exit(0);
        } else {
            // 新建一个students表的描述
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            // 在描述里添加列族
            for (String columnFamily : columnFamilys) {
                tableDesc.addFamily(new HColumnDescriptor(columnFamily));
            }
            // 根据配置好的描述建表
            HBaseClient.getHBaseAdmin().createTable(tableDesc);
            System.out.println("创建表 "+tableName+" 成功!");
            HBaseClient.getHBaseAdmin().close();
        }
    }
    /**
     * 新建列族
     */
    public void addColumnFamily(String tableName,String Name) throws IOException{

        System.out.println("---------------新建列族 START-----------------");

        // 取得目标数据表的表名对象
        TableName table= TableName.valueOf(tableName);

        // 创建列族对象
        HColumnDescriptor columnDescriptor = new HColumnDescriptor(Name);

        // 将新创建的列族添加到指定的数据表
        HBaseClient.getHBaseAdmin().addColumn(table, columnDescriptor);

        System.out.println("---------------新建列族 END-----------------");
    }
    /**
     * 删除列族
     */
    public void deleteColumnFamily(String tableName,String Name) throws IOException{

        System.out.println("---------------删除列族 START-----------------");

        // 取得目标数据表的表名对象
        TableName table = TableName.valueOf(tableName);

        // 删除指定数据表中的指定列族
        HBaseClient.getHBaseAdmin().deleteColumn(table, Name.getBytes());

        System.out.println("---------------删除列族 END-----------------");
    }
    // 删除数据库表
    //disable 'student'
    //drop 'student'
    public static void deleteTable(String tableName) throws Exception {
        // 新建一个数据库管理员
        if (HBaseClient.getHBaseAdmin().tableExists(TableName.valueOf(tableName))) {
            // 关闭一个表
            HBaseClient.getHBaseAdmin().disableTable(TableName.valueOf(tableName));
            HBaseClient.getHBaseAdmin().deleteTable(TableName.valueOf(tableName));
            System.out.println("删除表 "+tableName+" 成功！");
        } else {
            System.out.println("删除的表 "+tableName+" 不存在！");
            System.exit(0);
        }
    }
    
	// 添加一条数据
    //put 'student','zpc', ' info:age ','20'
	public static void addRow(String tableName, String row,
			String columnFamily, String column, String value) throws Exception {
		Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
		// 需要插入数据库的数据集合
		List<Put> putList = new ArrayList<Put>();
		Put put;
		put = new Put(Bytes.toBytes(row));
		put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
		putList.add(put);

		// 将数据集合插入到数据库
		table.put(putList);
	}

    // 获取一条数据
	//get 'student','zpc'
    public static void getRow(String tableName, String row) throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        // 输出结果,raw方法返回所有keyvalue数组        
        for (Cell cell : result.rawCells()) {
            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.print("时间戳:" + cell.getTimestamp() + " ");
            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
        }
        table.close();
    }
    public static void getRowByColumn(String tableName, String row, String family ,String column) throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(row));
        get.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
        Result result = table.get(get);
        // 输出结果,raw方法返回所有keyvalue数组        
        for (Cell cell : result.rawCells()) {
            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.print("时间戳:" + cell.getTimestamp() + " ");
            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
        }
        table.close();
    }
    public static void getRowByFamily(String tableName, String row, String family ) throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(row));
        get.addFamily(Bytes.toBytes(family));
        Result result = table.get(get);
        // 输出结果,raw方法返回所有keyvalue数组        
        for (Cell cell : result.rawCells()) {
            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.print("时间戳:" + cell.getTimestamp() + " ");
            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
        }
        table.close();
    }
    //判断表是否存在
    private static boolean isExist(String tableName) throws IOException {
        return HBaseClient.getHBaseAdmin().tableExists(TableName.valueOf(tableName));
    }
    // 获取所有数据
    public static void getAllRows(String tableName) throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner results = table.getScanner(scan);
        // 输出结果
        for (Result result : results) {
        	//一行结果
			for (Cell cell : result.rawCells()) {
	            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
	            System.out.print("时间戳:" + cell.getTimestamp() + " ");
	            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
	            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
	            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
	        }
        }
    }
    public static void getAllRowsByRowRange(String tableName,String startRow,String stopRow) throws Exception {
       
		Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        ResultScanner results = table.getScanner(scan);
        // 输出结果
        for (Result result : results) {
        	//一行结果
			for (Cell cell : result.rawCells()) {
	            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
	            System.out.print("时间戳:" + cell.getTimestamp() + " ");
	            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
	            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
	            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
	        }
        }
    }
    public static void getAllRowsByTimeRange(String tableName,long minStamp,long maxStamp) throws Exception {
        
 		Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
         Scan scan = new Scan();
		scan.setTimeRange(minStamp, maxStamp);
         ResultScanner results = table.getScanner(scan);
         // 输出结果
         for (Result result : results) {
         	//一行结果
 			for (Cell cell : result.rawCells()) {
 	            System.out.print("行名:" + new String(CellUtil.cloneRow(cell)) + " ");
 	            System.out.print("时间戳:" + cell.getTimestamp() + " ");
 	            System.out.print("列族名:" + new String(CellUtil.cloneFamily(cell)) + " ");
 	            System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
 	            System.out.println("值:" + new String(CellUtil.cloneValue(cell)));
 	        }
         }
     }
    // 删除一条(行)数据
    public static void delRow(String tableName, String row) throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        Delete del = new Delete(Bytes.toBytes(row));
        table.delete(del);
    }
 
    // 删除多条数据
    public static void delMultiRows(String tableName, String[] rows)
            throws Exception {
        Table table = HBaseClient.getHBaseConnection().getTable(TableName.valueOf(tableName));
        List<Delete> delList = new ArrayList<Delete>();
        for (String row : rows) {
            Delete del = new Delete(Bytes.toBytes(row));
            delList.add(del);
        }
        table.delete(delList);
    }
}