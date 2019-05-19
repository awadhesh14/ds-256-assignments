package in.ds256.Assignment0;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.ds256.tparserfinal.*;
import scala.Tuple2;
/**
 * DS-256 Assignment 0
 * Code for generating frequency distribution per hashtag
 */
public class FreqTag  {
	
	public static void main(String[] args) throws IOException {	
		
		String inputFile = args[0]; // Should be some file on HDFS
		String outputFile = args[1]; // Should be some file on HDFS
		
		SparkConf sparkConf = new SparkConf().setAppName("FreqTag");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);

		/**
		 * Code goes here
	 	 // */			//ddedededededede
		ObjectMapper objectMapper = new ObjectMapper();
		
		//open file
		JavaRDD <String> twitterdata = sc.textFile(inputFile);
		System.out.println("Program_Log: File Opened !");
		
		JavaPairRDD<Long, Tuple2> hashCount = twitterdata.flatMapToPair(f -> {
			TParserFinal tParser = objectMapper.readValue(f, TParserFinal.class);
			objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
			return Collections.singletonList(new Tuple2<Long,Tuple2> (tParser.getUser().getId() , new Tuple2<>(tParser.getEntities().getHashtags().size(), 1))).iterator();
		});
		System.out.println("Program_Log: Obtained Count !");
		
		JavaRDD<Double> avgPerUser = hashCount.reduceByKey((x, y) -> new Tuple2<>((Integer) x._1 + (Integer) y._1, (Integer) x._2 + (Integer) y._2)).map(x -> ((Integer) x._2._1).doubleValue() / ((Integer) x._2._2).doubleValue());
		JavaDoubleRDD avgPerUserD = avgPerUser.mapToDouble(x -> x);
		Tuple2<double[], long[]> histogram = avgPerUserD.histogram(20);
		
		 Configuration conf = new Configuration();
	        FileSystem fs = FileSystem.get(URI.create(outputFile), conf);
	        FSDataOutputStream out = fs.create(new Path(outputFile));
	        for(int i=0; i<20; i++) {
	            out.write((histogram._1[i]+",").getBytes());
	            out.write((histogram._2[i]+"").getBytes());
	            out.write(("\n").getBytes());
	        }
	        out.close();

	        System.out.println("Program_Log: Output written to file !");
		
		
		sc.stop();
		sc.close();
	}

}