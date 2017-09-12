package com.hadoop;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.common.DDBCar;
import com.common.DDBUser;
import com.util.Config;
import com.util.DateUtil;
import com.util.HdfsUtil;
/**
 * 同步车辆库和用户库到hdfs
 * date: 2017年4月20日 上午9:17:24 
 * @author geyang
 */
public class SqlToHdfs {
	static String start_date=DateUtil.date2Str(DateUtil.getDayBefore(31));
	static String sql= "sale_status=0 and flag!=-1 and modified_time>'"+start_date+"'";
	static Configuration conf=HdfsUtil.getConfiguration();
	public void run() {
		try {
			
	        ExecutorService service=Executors.newSingleThreadScheduledExecutor();
	        service.submit(new executeCar0());
	        service.submit(new executeCar1());
	        service.submit(new executeCar2());
	        service.submit(new executeCar3());
	        
//	        service.submit(new executeUser0());
//	        service.submit(new executeUser1());
//	        service.submit(new executeUser2());
//	        service.submit(new executeUser3());
	        
//			Thread thread0 = new Thread(new executeCar0());
//			Thread thread1 = new Thread(new executeCar0());
//			Thread thread2 = new Thread(new executeCar0());
//			Thread thread3 = new Thread(new executeCar0());
//			thread0.start();
//			thread0.join();
//			thread1.start();
//			thread1.join();
//			thread2.start();
//			thread2.join();
//			thread3.start();
//			thread3.join();
			// logger.error("MrUrl0");
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	public static class MapperCarMulti extends Mapper<Object, DDBCar, Text, Text> {
//		private MultipleOutputs<Text, Text> out;
//
//		protected void setup(Context context) throws IOException, InterruptedException {
//			out = new MultipleOutputs<Text, Text>(context);
//		}
		@Override
		protected void map(Object key, DDBCar value, Mapper<Object, DDBCar, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			context.write(new Text(value.toString()), new Text());
//			out.write("cars0000", value.toString(), new Text());
		}

//		protected void cleanup(Context context) throws IOException, InterruptedException {
//			out.close();
//		}
	}
	public static class MapperUserMulti extends Mapper<Object, DDBUser, Text, Text> {
//		private MultipleOutputs<Text, Text> out;
//
//		protected void setup(Context context) throws IOException, InterruptedException {
//			out = new MultipleOutputs<Text, Text>(context);
//		}
		@Override
		protected void map(Object key, DDBUser value, Mapper<Object, DDBUser, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			context.write(new Text(value.toString()), new Text());
//			out.write("cars0000", value.toString(), new Text());
		}

//		protected void cleanup(Context context) throws IOException, InterruptedException {
//			out.close();
//		}
	}
	public static class executeCar0 implements Runnable {
		private static final String OUT_PATH = "/carmulti/cars/cars0000";

		public void run() {

			try {
			
				// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
				// 通过conf创建数据库配置信息
				DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3306"),
						Config.getString("USER"), Config.getString("PASSWORD"));

				// 创建文件系统
				FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

				// 如果输出目录存在就删除
				if (fileSystem.exists(new Path(OUT_PATH))) {
					fileSystem.delete(new Path(OUT_PATH), true);
				}
				// 创建任务
					Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
				// 1.1 设置输入数据格式化的类和设置数据来源
				job.setInputFormatClass(DBInputFormat.class);
				String[] fields = {"id", "orderedr", "firstlist", "xujia",
						 "flag", "brand_str","type_str", "money", " big_type", "car_serial", "car_type", "car_kind"
						, "license ", "price_region", "car_source","car_foruse", "seats ","oil_wear  ", "transfer",
						"mortgage", "journey", "car_auto", "color",
						"zhiliao", "car_loginname", "user_id",
						 "click_count","usedate  ", "sale_status", 
						"area_code","new_id", "create_ip", "create_time", "modified_time"};
				DBInputFormat.setInput(job, DDBCar.class,"cars_0000", sql , null, fields);
//				DBInputFormat.setInput(job, DDBCar.class,  "select id from cars_0000 where sale_status=0 and flag!=-1 and modified_time>="+start_date, "select count(id) from cars_0001 where sale_status=0 and flag!=-1 and modified_time>="+start_date);

				// (job, Student.class, "student", null, null, new
				// String[]{"id","name"});

				// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
				job.setMapperClass(MapperCarMulti.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);

				// 1.3
				// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
				job.setNumReduceTasks(0);

				// 1.4 排序、分组
				// 1.5 归约
				// 2.1 Shuffle把数据从Map端拷贝到Reduce端

				// 2.2 指定Reducer类和输出key和value的类型
//				job.setReducerClass(IntSumReducer.class);

				// 2.3 指定输出的路径和设置输出的格式化类
//				MultipleOutputs.addNamedOutput(job, "cars0000", TextOutputFormat.class, Text.class, Text.class);
				FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
				job.waitForCompletion(true);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static class executeCar1 implements Runnable {
		private static final String OUT_PATH = "/carmulti/cars/cars0001";

		public void run() {

			try {
				
				// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
				// 通过conf创建数据库配置信息
				DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3306"),
						Config.getString("USER"), Config.getString("PASSWORD"));

				// 创建文件系统
				FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

				// 如果输出目录存在就删除
				if (fileSystem.exists(new Path(OUT_PATH))) {
					fileSystem.delete(new Path(OUT_PATH), true);
				}
				// 创建任务
					Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
				// 1.1 设置输入数据格式化的类和设置数据来源
				job.setInputFormatClass(DBInputFormat.class);
				String[] fields = {"id", "orderedr", "firstlist", "xujia",
						 "flag", "brand_str","type_str", "money", " big_type", "car_serial", "car_type", "car_kind"
						, "license ", "price_region", "car_source","car_foruse", "seats ","oil_wear  ", "transfer",
						"mortgage", "journey", "car_auto", "color",
						"zhiliao", "car_loginname", "user_id",
						 "click_count","usedate  ", "sale_status", 
						"area_code","new_id", "create_ip", "create_time", "modified_time"};
				DBInputFormat.setInput(job, DDBCar.class, "cars_0001",  sql, null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

				// (job, Student.class, "student", null, null, new
				// String[]{"id","name"});

				// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
				job.setMapperClass(MapperCarMulti.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);

				// 1.3
				// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
				job.setNumReduceTasks(0);

				// 1.4 排序、分组
				// 1.5 归约
				// 2.1 Shuffle把数据从Map端拷贝到Reduce端

				// 2.2 指定Reducer类和输出key和value的类型
//				job.setReducerClass(IntSumReducer.class);

				// 2.3 指定输出的路径和设置输出的格式化类
//				MultipleOutputs.addNamedOutput(job, "cars0001", TextOutputFormat.class, Text.class, Text.class);
				FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
				job.waitForCompletion(true);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static class executeCar2 implements Runnable {
		private static final String OUT_PATH = "/carmulti/cars/cars0002";

		public void run() {

			try {
				
				// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
				// 通过conf创建数据库配置信息
				DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3307"),
						Config.getString("USER"), Config.getString("PASSWORD"));

				// 创建文件系统
				FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

				// 如果输出目录存在就删除
				if (fileSystem.exists(new Path(OUT_PATH))) {
					fileSystem.delete(new Path(OUT_PATH), true);
				}
				// 创建任务
					Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
				// 1.1 设置输入数据格式化的类和设置数据来源
				job.setInputFormatClass(DBInputFormat.class);
				String[] fields = {"id", "orderedr", "firstlist", "xujia",
						 "flag", "brand_str","type_str", "money", " big_type", "car_serial", "car_type", "car_kind"
						, "license ", "price_region", "car_source","car_foruse", "seats ","oil_wear  ", "transfer",
						"mortgage", "journey", "car_auto", "color",
						"zhiliao", "car_loginname", "user_id",
						 "click_count","usedate  ", "sale_status", 
						"area_code","new_id", "create_ip", "create_time", "modified_time"};
				DBInputFormat.setInput(job, DDBCar.class, "cars_0002", sql, null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

				// (job, Student.class, "student", null, null, new
				// String[]{"id","name"});

				// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
				job.setMapperClass(MapperCarMulti.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);

				// 1.3
				// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
				job.setNumReduceTasks(0);

				// 1.4 排序、分组
				// 1.5 归约
				// 2.1 Shuffle把数据从Map端拷贝到Reduce端

				// 2.2 指定Reducer类和输出key和value的类型
//				job.setReducerClass(IntSumReducer.class);

				// 2.3 指定输出的路径和设置输出的格式化类
//				MultipleOutputs.addNamedOutput(job, "cars0002", TextOutputFormat.class, Text.class, Text.class);
				FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
				job.waitForCompletion(true);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static class executeCar3 implements Runnable {
		private static final String OUT_PATH = "/carmulti/cars/cars0003";

		public void run() {

			try {
				
				// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
				// 通过conf创建数据库配置信息
				DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3307"),
						Config.getString("USER"), Config.getString("PASSWORD"));

				// 创建文件系统
				FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

				// 如果输出目录存在就删除
				if (fileSystem.exists(new Path(OUT_PATH))) {
					fileSystem.delete(new Path(OUT_PATH), true);
				}
				// 创建任务
					Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
				// 1.1 设置输入数据格式化的类和设置数据来源
				job.setInputFormatClass(DBInputFormat.class);
				String[] fields = {"id", "orderedr", "firstlist", "xujia",
						 "flag", "brand_str","type_str", "money", " big_type", "car_serial", "car_type", "car_kind"
						, "license ", "price_region", "car_source","car_foruse", "seats ","oil_wear  ", "transfer",
						"mortgage", "journey", "car_auto", "color",
						"zhiliao", "car_loginname", "user_id",
						 "click_count","usedate  ", "sale_status", 
						"area_code","new_id", "create_ip", "create_time", "modified_time"};
				DBInputFormat.setInput(job, DDBCar.class, "cars_0003",sql, null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//				DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

				// (job, Student.class, "student", null, null, new
				// String[]{"id","name"});

				// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
				job.setMapperClass(MapperCarMulti.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);

				// 1.3
				// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
				job.setNumReduceTasks(0);

				// 1.4 排序、分组
				// 1.5 归约
				// 2.1 Shuffle把数据从Map端拷贝到Reduce端

				// 2.2 指定Reducer类和输出key和value的类型
//				job.setReducerClass(IntSumReducer.class);

				// 2.3 指定输出的路径和设置输出的格式化类
//				MultipleOutputs.addNamedOutput(job, "cars0003", TextOutputFormat.class, Text.class, Text.class);
				FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
				job.waitForCompletion(true);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	//---------------------------------------------------------------------------------------------------
		public static class executeUser0 implements Runnable {
			private static final String OUT_PATH = "/carmulti/users/users0000";

			public void run() {

				try {
				
					// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
					// 通过conf创建数据库配置信息
					DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3306"),
							Config.getString("USER"), Config.getString("PASSWORD"));

					// 创建文件系统
					FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

					// 如果输出目录存在就删除
					if (fileSystem.exists(new Path(OUT_PATH))) {
						fileSystem.delete(new Path(OUT_PATH), true);
					}
					// 创建任务
						Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
					// 1.1 设置输入数据格式化的类和设置数据来源
					job.setInputFormatClass(DBInputFormat.class);
					String[] fields = {"id", "star", "credit", "flag",
							 "user_type", "other_type","login_name", "company",  "problem", "answer", "username"
							,  "gender", "birthday ","area_code  ", "address",
							"mapurl", "longitude", "latitude", "post", " mobile",
							"phone", "email", "fax",
							"website", "manager_id","reg_ip  ", "clickcount", 
							"reg_date", "modified_time"};
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "car_loginname='admin'", null, fields);
					DBInputFormat.setInput(job, DDBUser.class, "users_0000", null, null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

					// (job, Student.class, "student", null, null, new
					// String[]{"id","name"});

					// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
					job.setMapperClass(MapperUserMulti.class);
					job.setMapOutputKeyClass(Text.class);
					job.setMapOutputValueClass(Text.class);

					// 1.3
					// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
					job.setNumReduceTasks(0);

					// 1.4 排序、分组
					// 1.5 归约
					// 2.1 Shuffle把数据从Map端拷贝到Reduce端

					// 2.2 指定Reducer类和输出key和value的类型
//					job.setReducerClass(IntSumReducer.class);

					// 2.3 指定输出的路径和设置输出的格式化类
//					MultipleOutputs.addNamedOutput(job, "users0000", TextOutputFormat.class, Text.class, Text.class);
					FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
					job.waitForCompletion(true);
				} catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();

				}

			}

			// }
		}
		public static class executeUser1 implements Runnable {
			private static final String OUT_PATH = "/carmulti/users/users0001";

			public void run() {

				try {
					
					// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
					// 通过conf创建数据库配置信息
					DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3306"),
							Config.getString("USER"), Config.getString("PASSWORD"));

					// 创建文件系统
					FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

					// 如果输出目录存在就删除
					if (fileSystem.exists(new Path(OUT_PATH))) {
						fileSystem.delete(new Path(OUT_PATH), true);
					}
					// 创建任务
						Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
					// 1.1 设置输入数据格式化的类和设置数据来源
					job.setInputFormatClass(DBInputFormat.class);
					String[] fields = {"id", "star", "credit", "flag",
							 "user_type", "other_type","login_name", "company",  "problem", "answer", "username"
							,  "gender", "birthday ","area_code  ", "address",
							"mapurl", "longitude", "latitude", "post", " mobile",
							"phone", "email", "fax",
							"website", "manager_id","reg_ip  ", "clickcount", 
							"reg_date", "modified_time"};
					DBInputFormat.setInput(job, DDBUser.class, "users_0001", null, null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

					// (job, Student.class, "student", null, null, new
					// String[]{"id","name"});

					// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
					job.setMapperClass(MapperUserMulti.class);
					job.setMapOutputKeyClass(Text.class);
					job.setMapOutputValueClass(Text.class);

					// 1.3
					// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
					job.setNumReduceTasks(0);

					// 1.4 排序、分组
					// 1.5 归约
					// 2.1 Shuffle把数据从Map端拷贝到Reduce端

					// 2.2 指定Reducer类和输出key和value的类型
//					job.setReducerClass(IntSumReducer.class);

					// 2.3 指定输出的路径和设置输出的格式化类
//					MultipleOutputs.addNamedOutput(job, "users0001", TextOutputFormat.class, Text.class, Text.class);
					FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
					job.waitForCompletion(true);
				} catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();

				}

			}

			// }
		}
		public static class executeUser2 implements Runnable {
			private static final String OUT_PATH = "/carmulti/users/users0002";

			public void run() {

				try {
					
					// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
					// 通过conf创建数据库配置信息
					DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3307"),
							Config.getString("USER"), Config.getString("PASSWORD"));

					// 创建文件系统
					FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

					// 如果输出目录存在就删除
					if (fileSystem.exists(new Path(OUT_PATH))) {
						fileSystem.delete(new Path(OUT_PATH), true);
					}
					// 创建任务
						Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
					// 1.1 设置输入数据格式化的类和设置数据来源
					job.setInputFormatClass(DBInputFormat.class);
					String[] fields = {"id", "star", "credit", "flag",
							 "user_type", "other_type","login_name", "company",  "problem", "answer", "username"
							,  "gender", "birthday ","area_code  ", "address",
							"mapurl", "longitude", "latitude", "post", " mobile",
							"phone", "email", "fax",
							"website", "manager_id","reg_ip  ", "clickcount", 
							"reg_date", "modified_time"};
					DBInputFormat.setInput(job, DDBUser.class, "users_0002", null, null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

					// (job, Student.class, "student", null, null, new
					// String[]{"id","name"});

					// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
					job.setMapperClass(MapperUserMulti.class);
					job.setMapOutputKeyClass(Text.class);
					job.setMapOutputValueClass(Text.class);

					// 1.3
					// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
					job.setNumReduceTasks(0);

					// 1.4 排序、分组
					// 1.5 归约
					// 2.1 Shuffle把数据从Map端拷贝到Reduce端

					// 2.2 指定Reducer类和输出key和value的类型
//					job.setReducerClass(IntSumReducer.class);

					// 2.3 指定输出的路径和设置输出的格式化类
//					MultipleOutputs.addNamedOutput(job, "users0002", TextOutputFormat.class, Text.class, Text.class);
					FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
					job.waitForCompletion(true);
				} catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();

				}

			}

			// }
		}
		public static class executeUser3 implements Runnable {
			private static final String OUT_PATH = "/carmulti/users/users0003";

			public void run() {

				try {
					
					// String date = DateUtil.date2Str(DateUtil.getDayBefore(31));
					// 通过conf创建数据库配置信息
					DBConfiguration.configureDB(conf,Config.getString("DRIVER"), Config.getString("URL_CARMULTI_3307"),
							Config.getString("USER"), Config.getString("PASSWORD"));

					// 创建文件系统
					FileSystem fileSystem = FileSystem.get(new URI(OUT_PATH), conf);

					// 如果输出目录存在就删除
					if (fileSystem.exists(new Path(OUT_PATH))) {
						fileSystem.delete(new Path(OUT_PATH), true);
					}
					// 创建任务
						Job job = HdfsUtil.getJob(SqlToHdfs.class.getSimpleName(),null);
					// 1.1 设置输入数据格式化的类和设置数据来源
					job.setInputFormatClass(DBInputFormat.class);
					String[] fields = {"id", "star", "credit", "flag",
							 "user_type", "other_type","login_name", "company",  "problem", "answer", "username"
							,  "gender", "birthday ","area_code  ", "address",
							"mapurl", "longitude", "latitude", "post", " mobile",
							"phone", "email", "fax",
							"website", "manager_id","reg_ip  ", "clickcount", 
							"reg_date", "modified_time"};
					DBInputFormat.setInput(job, DDBUser.class, "users_0003", null, null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0000", "car_loginname='admin'", null, fields);
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select * from cars_0001", "SELECT COUNT(*) FROM cars_0001");
//					DBInputFormat.setInput(job, DDBCar.class, "cars_0001", "select id,orderedr from cars_0001 LIMIT 10", null);

					// (job, Student.class, "student", null, null, new
					// String[]{"id","name"});

					// 1.2 设置自定义的Mapper类和Mapper输出的key和value的类型
					job.setMapperClass(MapperUserMulti.class);
					job.setMapOutputKeyClass(Text.class);
					job.setMapOutputValueClass(Text.class);

					// 1.3
					// 设置分区和reduce数量(reduce的数量和分区的数量对应，因为分区只有一个，所以reduce的个数也设置为一个)
					job.setNumReduceTasks(0);

					// 1.4 排序、分组
					// 1.5 归约
					// 2.1 Shuffle把数据从Map端拷贝到Reduce端

					// 2.2 指定Reducer类和输出key和value的类型
//					job.setReducerClass(IntSumReducer.class);

					// 2.3 指定输出的路径和设置输出的格式化类
//					MultipleOutputs.addNamedOutput(job, "users0003", TextOutputFormat.class, Text.class, Text.class);
					FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
					job.waitForCompletion(true);
				} catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();

				}

			}

			// }
		}
	public static void main(String[] args) {
		SqlToHdfs sqlToHdfs=new SqlToHdfs();
//		Thread thread = new Thread(new SqlToHdfs());
		sqlToHdfs.run();
	}
}
