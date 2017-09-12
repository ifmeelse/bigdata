package com.hive;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.SQLException;  
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.util.DateUtil;  

/** 
 * Hive的JavaApi 
 *  
 * 启动hive的远程服务接口命令行执行：hive --service hiveserver & 
 *  
 * @author geyang
 *  
 */  
public class HiveJdbcCli {  
    //网上写 org.apache.hadoop.hive.jdbc.HiveDriver ,新版本不能这样写
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";  

    
    
  //这里是hive2，网上其他人都写hive,在高版本中会报错
    private static String url = "jdbc:hive2://10.90.60.206:10000/default"; 
    private static String user = "hive";  
//    private static String password = "e10adc3949ba59abbe56e057f20f883e";  
    private static String password = "hx2carhive"; 
    private static String sql = "";  
    private static ResultSet res;  
    static String tableName = "accesslog"; 
    private static final Logger log = Logger.getLogger(HiveJdbcCli.class);  

    public static void main(String[] args) {  
        Connection conn = null;  
        Statement stmt = null;  
        try {  
            conn = getConn();  
            stmt = conn.createStatement();  

            // 第一步:存在就先删除  
//            String tableName = dropTable(stmt);  

            // 第二步:不存在就创建  
//            createTable(stmt, tableName);  
              //创建外部表
//            createExternalTable(stmt, tableName); 
            
            // 第三步:查看创建的表  
            showTables(stmt, tableName);  

            // 执行describe table操作  
            describeTables(stmt, tableName);  

            // 执行load data into table操作  
//            loadData(stmt, tableName);  

            //加载外部表数据
//            loadHdfsData(stmt, tableName);
            
            // 执行 select * query 操作  
//            selectData(stmt, tableName);  

            // 执行 regular hive query 统计操作  
//            countData(stmt, tableName);  

        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
            log.error(driverName + " not found!", e);  
            System.exit(1);  
        } catch (SQLException e) {  
            e.printStackTrace();  
            log.error("Connection error!", e);  
            System.exit(1);  
        } finally {  
            try {  
                if (conn != null) {  
                    conn.close();  
                    conn = null;  
                }  
                if (stmt != null) {  
                    stmt.close();  
                    stmt = null;  
                }  
            } catch (SQLException e) {  
                e.printStackTrace();  
            } 
            System.exit(0);
        }  
    }  

    private static void countData(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "select count(1) from " + tableName;  
        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        System.out.println("执行“regular hive query”运行结果:");  
        while (res.next()) {  
            System.out.println("count ------>" + res.getString(1));  
        }  
    }  

    private static void selectData(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "select * from " + tableName;  
        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        System.out.println("执行 select * query 运行结果:");  
        while (res.next()) {  
            System.out.println(res.getString(1) + "\t" + res.getString(2));  
        }  
    }  
    
    private static void loadData(Statement stmt, String tableName)  
            throws SQLException {  
        //目录 ，我的是hive安装的机子的虚拟机的home目录下
        String filepath = "web_access.log";  
        sql = "load data local inpath '" + filepath + "' into table "  
                + tableName;  
        System.out.println("Running:" + sql);  
         stmt.execute(sql);  
    }  
    private static void loadHdfsData(Statement stmt, String tableName)  
            throws SQLException {  
        //目录 ，我的是hive安装的机子的虚拟机的home目录下 overwrite会覆盖
    	String date = DateUtil.date2Str(DateUtil.getBeforeDay());
        String filepath = "/data_log/web_all/"+date+"/";  
        sql = "alter table "+tableName+" add partition(dt='"+date+"') location '"+filepath+"'";  
        System.out.println("Running:" + sql);  
         stmt.execute(sql);  
    } 
    private static void describeTables(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "describe " + tableName;  
        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        System.out.println("执行 describe table 运行结果:");  
        while (res.next()) {  
            System.out.println(res.getString(1) + "\t" + res.getString(2));  
        }  
    }  

    private static void showTables(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "show tables '" + tableName + "'";  
        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        System.out.println("执行 show tables 运行结果:");  
        if (res.next()) {  
            System.out.println(res.getString(1));  
        }  
    }  

    private static void createTable(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "create table  if not exists "  
                + tableName  
                + " (userId string, fromIp string, country string, province string,"
                + " city string, area string, company string, accessUrl string, fromUrl string, "
                + "accessType string,buildOriginalURL string,uniqueCookie string) partitioned by (dt string) row format delimited fields terminated by '|'";  
        stmt.execute(sql);  
    }  
    private static void createExternalTable(Statement stmt, String tableName)  
            throws SQLException {  
        sql = "create external table  if not exists "  
                + tableName  
                + " (time string, fromIp string, country string, province string,"
                + " city string, area string, company string, accessUrl string, fromUrl string, "
                + "accessType string,buildOriginalURL string,uniqueCookie string,mobile string,userId string) partitioned by (dt string)  row format delimited fields terminated by '|'";
//                + "LOCATION '/data_log/web_log/'";  
        stmt.execute(sql);  
    }
    private static String dropTable(Statement stmt) throws SQLException {  
        // 创建的表名  
//        String tableName = "accesslog";   
        sql = "drop table  " + tableName;  
        stmt.execute(sql);  
        return tableName;  
    }  

    private static Connection getConn() throws ClassNotFoundException,  
            SQLException {  
        Class.forName(driverName);  
        Connection conn = DriverManager.getConnection(url, user, password);  
        return conn;  
    }  

}  