package com.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
public class SelectHive {  
    //网上写 org.apache.hadoop.hive.jdbc.HiveDriver ,新版本不能这样写
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";  
    private static ResultSet res;  
  //这里是hive2，网上其他人都写hive,在高版本中会报错
    private static String url = "jdbc:hive2://10.90.60.206:10000/default"; 
    private static String user = "hive";  
//    private static String password = "e10adc3949ba59abbe56e057f20f883e";  
    private static String password = "hx2carhive"; 
    static String tableName = "accesslog"; 
    private static final Logger log = Logger.getLogger(SelectHive.class);  

    public static void main(String[] args) {  
        Connection conn = null;  
        Statement stmt = null;  
        try {
        	conn = getConn();  
            stmt = conn.createStatement();  
            //加载外部表数据
            countData(stmt, tableName);  
        	
        } catch (Exception e) {  
            e.printStackTrace();  
            log.error(driverName + " not found!", e);  
            System.exit(1);  
        }  finally {  
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
    	String date = DateUtil.date2Str(DateUtil.getBeforeDay());
//    	String date ="2017-05-12";
       String sql = "select count(1) from " + tableName+" where dt='"+date+"'";  
        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        System.out.println("执行“regular hive query”运行结果:");  
        while (res.next()) {  
            System.out.println("count ------>" + res.getString(1));  
        } 
        stmt.close();
    }  

    private static Connection getConn() throws ClassNotFoundException,  
            SQLException {  
        Class.forName(driverName);  
        Connection conn = DriverManager.getConnection(url, user, password);  
        return conn;  
    }  

}  