package com.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.util.DateUtil;

import scala.reflect.internal.Trees.New;
import shapeless.newtype;  

/** 
 * Hive的JavaApi 
 *  
 * 启动hive的远程服务接口命令行执行：hive --service hiveserver & 
 *  
 * @author geyang
 *  
 */  
public class LoadToHive {  
    //网上写 org.apache.hadoop.hive.jdbc.HiveDriver ,新版本不能这样写
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";  
    private static String sql = "";  
    private static ResultSet res;  
  //这里是hive2，网上其他人都写hive,在高版本中会报错
    private static String url = "jdbc:hive2://10.90.60.206:10000/default"; 
    private static String user = "root";  
//    private static String password = "e10adc3949ba59abbe56e057f20f883e";  
    private static String password = ""; 
    static String tableName = "accesslog"; 
    private static final Logger log = Logger.getLogger(LoadToHive.class);  
    static String date = DateUtil.date2Str(DateUtil.getBeforeDay(-2));
    public static void main(String[] args) {  
        Connection conn = null;  
        Statement stmt = null;  
        try {
        	conn = getConn();  
            stmt = conn.createStatement();  
            //加载外部表数据
          boolean flag= isExistPartitions(stmt, tableName);
          System.out.println(flag);
          if (flag) {
//            loadHdfsData(stmt, tableName);
			
		}
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
            } 
            System.exit(0);
        }  
    }  
    private static boolean isExistPartitions(Statement stmt, String tableName)  
            throws SQLException {  
    	  sql = "show partitions " + tableName ; 
//        System.out.println("Running:" + sql);  
        res = stmt.executeQuery(sql);  
        while (res.next()) {  
        	System.out.println(res.getObject(1));  
//        	if (res.getString(1).contains(date)) {
//				return false;
//			}
        }  
        return true;
    }  
    private static void loadHdfsData(Statement stmt, String tableName)  
            throws SQLException {  
        //目录 ，我的是hive安装的机子的虚拟机的home目录下 overwrite会覆盖
    	
        String filepath = "/data_log/web_all/"+date+"/";  
      sql = "alter table "+tableName+" add partition(dt='"+date+"') location '"+filepath+"'";  
        System.out.println("Running:" + sql);  
         stmt.execute(sql);  
    } 

    private static Connection getConn() throws ClassNotFoundException,  
            SQLException {  
        Class.forName(driverName);  
        Connection conn = DriverManager.getConnection(url, user, password);  
        return conn;  
    }  

}  