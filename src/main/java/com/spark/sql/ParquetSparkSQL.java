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
import org.apache.spark.api.java.function.MapFunction;
// $example off:programmatic_schema$
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
// $example on:init_session$
import org.apache.spark.sql.SparkSession;

public class ParquetSparkSQL {


  public static void main(String[] args) throws AnalysisException {
		SparkConf conf = new SparkConf();
		conf.setAppName("Jdbc Spark SQL basic example");
		SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
		 Dataset<Row> usersDF = spark.read().load("examples/src/main/resources/users.parquet");
		    usersDF.select("name", "favorite_color").write().save("namesAndFavColors.parquet");
		    // $example off:generic_load_save_functions$
		    // $example on:manual_load_options$
		    Dataset<Row> peopleDF =
		      spark.read().format("json").load("examples/src/main/resources/people.json");
		    peopleDF.select("name", "age").write().format("parquet").save("namesAndAges.parquet");
		    // $example off:manual_load_options$
		    // $example on:direct_sql$
		    Dataset<Row> sqlDF =
		      spark.sql("SELECT * FROM parquet.`examples/src/main/resources/users.parquet`");
		    Dataset<Row> peopleDF1 = spark.read().json("examples/src/main/resources/people.json");

		    // DataFrames can be saved as Parquet files, maintaining the schema information
		    peopleDF1.write().parquet("people.parquet");

		    // Read in the Parquet file created above.
		    // Parquet files are self-describing so the schema is preserved
		    // The result of loading a parquet file is also a DataFrame
		    Dataset<Row> parquetFileDF = spark.read().parquet("people.parquet");

		    // Parquet files can also be used to create a temporary view and then used in SQL statements
		    parquetFileDF.createOrReplaceTempView("parquetFile");
		    Dataset<Row> namesDF = spark.sql("SELECT name FROM parquetFile WHERE age BETWEEN 13 AND 19");
		    Dataset<String> namesDS = namesDF.map(new MapFunction<Row, String>() {
		      public String call(Row row) {
		        return "Name: " + row.getString(0);
		      }
		    }, Encoders.STRING());
		    namesDS.show();
  
  
  }

  
}
