package com.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;  
  
public class HBaseClient {  
	// 声明静态配置
	static Connection connection;
	  static Admin hAdmin;

	static synchronized Connection getHBaseConnection() {
		if (connection == null) {
			// 配置自己HBase的环境，其实主要是ZOOKEEPER的一些参数
			Configuration conf = HBaseConfiguration.create();
			// conf.set(HConstants.ZOOKEEPER_QUORUM,
			// "hadoop2,hadoop3,hadoop4,hadoop5");
			// conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, "2222");2181
//			conf.set("hbase.master", "10.90.60.206");
			conf.set("hbase.zookeeper.property.clientPort", "2181");
			conf.set("hbase.zookeeper.quorum", "10.90.60.206,10.90.60.205,10.90.60.204");
			try {
				connection = ConnectionFactory.createConnection(conf);
				// 取得一个数据库元数据操作对象
			} catch (IOException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		return connection;

	}
    static synchronized Admin getHBaseAdmin(){
        try {
        	if (hAdmin==null) {
        		hAdmin = getHBaseConnection().getAdmin();

			}
			 // 取得一个数据库元数据操作对象
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return hAdmin;

    }
   
}