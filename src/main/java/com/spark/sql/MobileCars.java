/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spark.sql;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
// $example off:programmatic_schema$
import org.apache.spark.sql.AnalysisException;
// $example on:create_df$
// $example on:run_sql$
// $example on:programmatic_schema$
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
// $example on:init_session$
import org.apache.spark.sql.SparkSession;

import com.util.DateUtil;

public class MobileCars {

	public static void main(String[] args) throws AnalysisException {
		 Logger logger = Logger.getLogger(MobileCars.class);
		SparkConf conf = new SparkConf();
		conf.setAppName("MobileCars");
		String date=DateUtil.date2Str(DateUtil.getBeforeDay(-30));
		SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
		// $example off:init_session$

		Dataset<Row> cars0 = spark.read().format("jdbc").option("url", "jdbc:mysql://10.90.60.212:3306/car_multi")
				.option("dbtable", "(SELECT car_type,money from cars_0000 WHERE modified_time>='"+date+"' and car_type>0 and money>0) as T").option("user", "onlyreader").option("password", "hx2car88212994")
				.load();
		
		Dataset<Row> cars1 = spark.read().format("jdbc").option("url", "jdbc:mysql://10.90.60.212:3306/car_multi")
				.option("dbtable", "(SELECT car_type,money  from cars_0001 WHERE modified_time>='"+date+"' and car_type>0 and money>0 ) as T").option("user", "onlyreader").option("password", "hx2car88212994")
				.load();
		
		Dataset<Row> cars2 = spark.read().format("jdbc").option("url", "jdbc:mysql://10.90.60.212:3307/car_multi")
				.option("dbtable", "(SELECT car_type,money  from cars_0002 WHERE modified_time>='"+date+"' and car_type>0 and money>0 ) as T").option("user", "onlyreader").option("password", "hx2car88212994")
				.load();
		
		Dataset<Row> cars3 = spark.read().format("jdbc").option("url", "jdbc:mysql://10.90.60.212:3307/car_multi")
				.option("dbtable", "(SELECT car_type,money  from cars_0003 WHERE modified_time>='"+date+"' and car_type>0 and money>0 ) as T").option("user", "onlyreader").option("password", "hx2car88212994")
				.load();
	

		
		Dataset<Row> all =cars0.union(cars1).union(cars2).union(cars3);
		all.createOrReplaceTempView("cars");
		
		
		Dataset<Row> allM =all.groupBy("car_type").count();
		allM.show();
		allM.toJavaRDD().repartition(1).saveAsTextFile("/test/car/");
//		LongAccumulator count = spark.sparkContext().longAccumulator();
//
//		
////		 Dataset<Row> alls2 =all.sqlContext().sql("SELECT car_loginname,car_serial,COUNT(car_serial) as count FROM cars GROUP BY car_loginname,car_serial ORDER BY car_loginname,count desc");
//
//		long alls =all.toJavaRDD().map(new Function<Row, Row>() {
//
//			@Override
//			public Row call(Row arg0) throws Exception {
//				
//				count.add(arg0.getInt(0));
//				return arg0;
//			}
//		}).count();
////		alls.show();
//		logger.error("mm:"+alls);
//		
//		
//		logger.error("mmcount:"+count.value());
//		
//		
//		logger.error("avg:"+count.avg());
//		
//		logger.error("mmcount:"+count.sum());
//		
//		logger.error("mmcount:"+count.id());
		
//		Dataset<Row> alls =all.groupBy("car_loginname","car_serial").count().sort("car_loginname","count");
//		
//		alls.toJavaRDD().repartition(1).saveAsTextFile("/cars/serial/"+DateUtil.date2Str(new Date()));
//		
//        Dataset<Row> alls2 =all.groupBy("car_loginname","money").count().sort("car_loginname","count");
//		
//        alls2.toJavaRDD().repartition(1).saveAsTextFile("/cars/money/"+DateUtil.date2Str(new Date()));
//				.filter(new FilterFunction<Row>() {
//			
//			public boolean call(Row arg0) throws Exception {
//				
//		if (arg0.get(0).equals("13375596678")) {
//			return true;
//		}				
//		return false;
//			}
//		}).sort("count");
		
//		alls.show();
		
		spark.stop();
	}


}
