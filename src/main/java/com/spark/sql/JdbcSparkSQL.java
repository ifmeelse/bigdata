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

// $example on:programmatic_schema$
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
// $example off:programmatic_schema$
// $example on:create_ds$
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;
// $example off:create_ds$

import org.apache.spark.SparkConf;
// $example on:schema_inferring$
// $example on:programmatic_schema$
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
// $example off:programmatic_schema$
// $example on:create_ds$
import org.apache.spark.api.java.function.MapFunction;
// $example on:create_df$
// $example on:run_sql$
// $example on:programmatic_schema$
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
// $example off:programmatic_schema$
// $example off:create_df$
// $example off:run_sql$
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
// $example off:create_ds$
// $example off:schema_inferring$
import org.apache.spark.sql.RowFactory;
// $example on:init_session$
import org.apache.spark.sql.SparkSession;
// $example off:init_session$
// $example on:programmatic_schema$
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
// $example off:programmatic_schema$
import org.apache.spark.sql.AnalysisException;

// $example on:untyped_ops$
// col("...") is preferable to df.col("...")
import static org.apache.spark.sql.functions.col;
// $example off:untyped_ops$

public class JdbcSparkSQL {

	public static void main(String[] args) throws AnalysisException {
		SparkConf conf = new SparkConf();
		conf.setAppName("Jdbc Spark SQL basic example");
		SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
		// $example off:init_session$

		Dataset<Row> jdbcDF = spark.read().format("jdbc").option("url", "jdbc:mysql://10.90.60.212:3306/car_only")
				.option("dbtable", "aliqin_user").option("user", "onlyreader").option("password", "hx2car88212994").load();
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "onlyreader");
		connectionProperties.put("password", "hx2car88212994");
		Dataset<Row> jdbcDF2 = spark.read().jdbc("jdbc:mysql://10.90.60.212:3306/car_only", "aliqin_user", connectionProperties);

		// Saving data to a JDBC source
		jdbcDF.show();
		jdbcDF2.show();
//		jdbcDF.write().format("jdbc").option("url", "jdbc:postgresql:dbserver").option("dbtable", "schema.tablename")
//				.option("user", "username").option("password", "password").save();

//		jdbcDF2.write().jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties);

		spark.stop();
	}

}
