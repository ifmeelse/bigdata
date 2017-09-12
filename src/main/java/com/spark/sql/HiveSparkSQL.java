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

import org.apache.spark.SparkConf;
// $example off:programmatic_schema$
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
// $example on:init_session$
import org.apache.spark.sql.SparkSession;

public class HiveSparkSQL {

	public static void main(String[] args) throws AnalysisException {
		// $example on:init_session$
		String warehouseLocation = "hdfs://mycluster/user/hive/warehouse";
		SparkConf conf = new SparkConf();
		conf.setAppName("Java Spark SQL basic example");
		conf.set("spark.sql.warehouse.dir", warehouseLocation);
		SparkSession spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate();
		String sql = "select count(1) from accesslog where dt= '2017-06-26' and buildOriginalURL like '%getWechatQr.json%'";
		Dataset<Row> sqlDF = spark.sql(sql);
		sqlDF.show();

		spark.stop();
	}


}
