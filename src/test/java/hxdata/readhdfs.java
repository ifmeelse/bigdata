/**
 * Project Name:hxdata
 * File Name:readhdfs.java
 * Package Name:hxdata
 * Date:2017年4月21日下午12:15:09
 * Copyright (c) 2017, hx2car.com All Rights Reserved.
 *
*/
/**
 * Project Name:hxdata
 * File Name:readhdfs.java
 * Package Name:hxdata
 * Date:2017年4月21日下午12:15:09
 * Copyright (c) 2017, geyang All Rights Reserved.
 *
 */

package hxdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * ClassName:readhdfs 
 * Function: TODO ADD FUNCTION 
 * Reason:	 TODO ADD REASON
 * Date:     2017年4月21日 下午12:15:09 
 * @author   geyang
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * date: 2017年4月21日 下午12:15:09 
 * @author geyang
 */
public class readhdfs {
public static void main(String[] args) throws IOException {
	Configuration conf = new Configuration();
	conf.set("fs.defaultFS", "hdfs://mycluster");
    conf.set("dfs.nameservices", "mycluster");
    conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
	conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
	conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
	conf.set("dfs.client.failover.proxy.provider.mycluster", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
    int i=0;
	String path = "/carmulti/cars/cars0000/part-m-00000";
	FileSystem fs = FileSystem.get(URI.create(path), conf);
	FSDataInputStream hdfsInStream = fs.open(new Path(path));
	InputStreamReader isr = new InputStreamReader(hdfsInStream, "utf-8");
	BufferedReader br = new BufferedReader(isr);
	String uline;
//	int k = 0;
	while ((uline = br.readLine()) != null) {
		System.out.println(uline);
		i++;
		if (i==10) {
			break;
		}
	}
}
}

