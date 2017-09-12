package com.spark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.AssociationRules.Rule;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowth.FreqItemset;
import org.apache.spark.mllib.fpm.FPGrowthModel;

import com.hbase.HBase;

import scala.Tuple2;

public final class WriteHbase {
	public static void main(String[] args) throws Exception {

		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("ReadHbase");
		sparkConf.setMaster("spark://10.90.60.205:7077,10.90.60.206:7077");
		sparkConf.set("spark.submit.deployMode", "cluster");
		sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		
	    JavaRDD<String> lines = sc.textFile(args[0]);
	    lines.foreachPartition(new VoidFunction<Iterator<String>>() {
			
			@Override
			public void call(Iterator<String> arg0) throws Exception {
				
//				HBase.addRow(tableName, row, columnFamily, column, value);
				
			}
		});

		
		
		
		sc.stop();
		sc.close();
	}
}