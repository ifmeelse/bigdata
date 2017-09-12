package com.spark;

import java.util.ArrayList;
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
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.AssociationRules.Rule;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowth.FreqItemset;
import org.apache.spark.mllib.fpm.FPGrowthModel;

import scala.Tuple2;

public final class ReadHbase {
	public static void main(String[] args) throws Exception {

		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("ReadHbase");
		sparkConf.setMaster("spark://10.90.60.205:7077,10.90.60.206:7077");
		sparkConf.set("spark.submit.deployMode", "cluster");
		sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		String tableName = "user_visits";
		Configuration conf = HBaseConfiguration.create();
		Scan scan = new Scan();
		// scan.setStartRow(Bytes.toBytes("1899998858720170808000000"));
		// scan.setStopRow(Bytes.toBytes( "1899998858720170810000000"));
		conf.set(TableInputFormat.INPUT_TABLE, tableName);
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		String ScanToString = Base64.encodeBytes(proto.toByteArray());
		conf.set(TableInputFormat.SCAN, ScanToString);

		JavaPairRDD<ImmutableBytesWritable, Result> myRDD = sc.newAPIHadoopRDD(conf, TableInputFormat.class,
				ImmutableBytesWritable.class, Result.class);
//		myRDD.count();
		JavaRDD<Result> data = myRDD.values();
		JavaRDD<List<String>> transactions = data.map(new Function<Result, List<String>>() {
			@Override
			public List<String> call(Result result) throws Exception {
				List<String> list = new ArrayList<>();
				for (Cell cell : result.rawCells()) {
					// System.out.print("行名:" + new
					// String(CellUtil.cloneRow(cell)) + " ");
					// System.out.print("时间戳:" + cell.getTimestamp() + " ");
					// System.out.print("列族名:" + new
					// String(CellUtil.cloneFamily(cell)) + " ");
//					 System.out.print("列名:" + new String(CellUtil.cloneQualifier(cell)) + " ");
					// System.out.println("值:" + new
					// String(CellUtil.cloneValue(cell)));
					list.add(new String(CellUtil.cloneQualifier(cell))+":"+new String(CellUtil.cloneValue(cell)));
				}

				return list;
			}
		});
		transactions.cache();
		FPGrowth fpg = new FPGrowth().setMinSupport(0.01).setNumPartitions(5);
		FPGrowthModel<String> model = fpg.run(transactions);

		for (FPGrowth.FreqItemset<String> itemset : model.freqItemsets().toJavaRDD().collect()) {
			System.out.println("[" + itemset.javaItems() + "], " + itemset.freq());
		}
//		JavaRDD<FreqItemset<String>> itemset =model.freqItemsets().toJavaRDD();
//		itemset.repartition(1).saveAsTextFile("/test/uuy1" + System.currentTimeMillis());

		
		double minConfidence = 0.1;
		
//		JavaRDD<Rule<String>> rule= model.generateAssociationRules(minConfidence).toJavaRDD();
		for (AssociationRules.Rule<String> rule : model.generateAssociationRules(minConfidence).toJavaRDD().collect()) {
			System.out.println(rule.javaAntecedent() + " => " + rule.javaConsequent() + ", " + rule.confidence());
		}

//		rule.repartition(1).saveAsTextFile("/test/uuy" + System.currentTimeMillis());
		sc.stop();
		sc.close();
	}
}