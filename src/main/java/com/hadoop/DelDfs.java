package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

import com.util.DateUtil;
import com.util.HdfsUtil;
/**
 * 定时删除30天以后日志
 * date: 2017年4月20日 上午9:16:19 
 * @author geyang
 */
public class DelDfs {
//	public void run() {
//		Thread thread = new Thread(new execute());
//		thread.start();
//		// logger.error("MrUrl0");
//	}

	public static class execute implements Runnable {
		public void run() {

			try {
//				Job job = HdfsUtil.getJob(DelDfs.class.getSimpleName(),null);
				Configuration conf=HdfsUtil.getConfiguration();
				// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
//				Path dstPath = new Path("/data_log/web_all/web_access.log." + DateUtil.date2Str(DateUtil.getDayBefore(32)));
				Path dstPath = new Path("/data_log/web_all/"+ DateUtil.date2Str(DateUtil.getDayBefore(32))) ;

				Path dstPath1 = new Path("/output_log/output_log_click" + DateUtil.date2Str(DateUtil.getDayBefore(3)));

				Path dstPath2 = new Path("/output_log/output_log_search" + DateUtil.date2Str(DateUtil.getDayBefore(3)));

				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				if (dhfs.exists(dstPath1)) {
					dhfs.delete(dstPath1, true);
				}
				if (dhfs.exists(dstPath2)) {
					dhfs.delete(dstPath2, true);
				}
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static void main(String[] args) {
		Thread thread = new Thread(new execute());
		thread.start();
	}
}
