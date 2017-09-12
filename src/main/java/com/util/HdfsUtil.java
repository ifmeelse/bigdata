package com.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.Job;

/**
 * message: 14
 * 
 * @author geyang
 *
 */
public class HdfsUtil {
	// private static String hdfsUri = "hdfs://10.90.60.206:9000";
	// private static String hdfsDir = "/data_log/web_log/";

	public static void WriteLocal(String str) {
		try {
			String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
			// String path =
			// DBUtil.class.getClassLoader().getResource("").getPath();
			// String path ="/app/soft/app/data/"+ date;
			File file = new File("web_access.log" + date);
			// if (!file.getParentFile().exists()) {
			// file.mkdirs();
			// }
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(str);
			bufferWritter.flush();
			bufferWritter.close();
			fileWritter.close();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	/**
	 * 
	 * 写入hdfs
	 *
	 * @param str
	 *            写入内容
	 * @param hdfsDir
	 *            hdfs目录
	 * @return
	 * @since JDK 1.6
	 */
	public static synchronized void WriteHDFS(String str, String hdfsDir, String fileName) {
		FileSystem hadoopFS = null;
		try {

			if (fileName != null && !"".equals(fileName)) {
				hdfsDir = hdfsDir + fileName;
			}

		
			hadoopFS = FileSystem.get(URI.create(hdfsDir), getConfiguration());
			Path dst = new Path(hdfsDir);
			if (!hadoopFS.exists(dst)) {
				hadoopFS.createNewFile(dst);
			}
			InputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
			FSDataOutputStream out = hadoopFS.append(dst);
			IOUtils.copyBytes(in, out, 4096, true);
			in.close();
			out.close();
		} catch (Exception e1) {

			// TODO Auto-generated catch block
			e1.printStackTrace();

		} finally {
			try {
				if (hadoopFS != null) {
					hadoopFS.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void copyFile(String local, String remote) throws IOException {
		File file = new File(local);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			return;
		}
		
		FileSystem fs = FileSystem.get(getConfiguration());
		fs.copyFromLocalFile(false, true, new Path(local), new Path(remote));
		// System.out.println("copy from: " + local + " to " + remote);
		fs.close();
	}

	public static void rename(String fromFile, String toFile) throws IOException {
		FileSystem fs = FileSystem.get(URI.create(fromFile), getConfiguration());
		fs.rename(new Path(fromFile), new Path(toFile));
		fs.close();
	}
	/**
	 * 删除文件或者文件夹
	 * 
	 * @param conf
	 * @param uri
	 * @param filePath
	 * @throws IOException
	 */
	   public static boolean deleteFile(String path) throws IOException{
			FileSystem fs = FileSystem.get(URI.create(path), getConfiguration());
	        return fs.delete(new Path(path),true);//false 为是否递归删除
	    }

	   public static boolean isExists(String path) throws IOException{
		   FileSystem fs = FileSystem.get(getConfiguration());
	        return fs.exists(new Path(path));
	    }
	public static ArrayList<String> getLogAccessByDay(int day) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= day; i++) {
			try {
				if (DateUtil.compareDate(sd.format(DateUtil.getSpecifiedDayBefore(-i)), "2017-05-07")) {
					if (isExists("/data_log/web_all/"+sd.format(DateUtil.getSpecifiedDayBefore(-i))+"/web_access.log" )) {
						list.add("/data_log/web_all/"+sd.format(DateUtil.getSpecifiedDayBefore(-i))+"/web_access.log" );
					}
				}else {
					list.add("/data_log/web_all/web_access.log." + sd.format(DateUtil.getSpecifiedDayBefore(-i)));	
				}
			} catch (IOException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
		}
		return list;

	}
	public static Job getJob(String name,Configuration conf) {
		try {
			if (conf==null) {
				conf=getConfiguration();
			}
			Job job = Job.getInstance(conf, name);
			job.addArchiveToClassPath(new Path("/jar/commons-lang3-3.5.jar"));
			job.addArchiveToClassPath(new Path("/jar/kafka-clients-0.10.2.1.jar"));
			job.addArchiveToClassPath(new Path("/jar/fastjson-1.2.9.jar"));

			return job;
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;

	}
	public static Configuration getConfiguration() {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://mycluster");
		conf.set("dfs.nameservices", "mycluster");
		conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
		conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
		conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
		conf.set("dfs.client.failover.proxy.provider.mycluster",
				"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
		conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.01");
		conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.01");
		return conf;

	}
	public static void main(String[] args) throws IOException {
		String path = "/data_log/web_log/" + DateUtil.date2Str(new Date()) + "/";

		System.out.println(deleteFile(path));
	}

}
