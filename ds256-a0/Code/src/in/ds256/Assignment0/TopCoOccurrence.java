package in.ds256.Assignment0;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.ds256.tparserfinal.TParserFinal;
import scala.Tuple2;
/**
 * DS-256 Assignment 0
 * Code for finding frequent co-occuring hash-tags
 */
public class TopCoOccurrence  {
	
	public static void main(String[] args) throws IOException {	
		
		String inputFile = args[0]; // Should be some file on HDFS
		String outputFile = args[1]; // Should be some file on HDFS
		
		SparkConf sparkConf = new SparkConf().setAppName("​TopCoOccurrence");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);

		/**
		 * Code goes here
	 	 */			
		ObjectMapper objectMapper = new ObjectMapper();
		//TParserFinal tParser;
		JavaRDD<String> twitterData = sc.textFile(inputFile);
		System.out.println("Program_Log: File Opened !");
		
		List<Tuple2<Long, Tuple2<String, String>>> pairs  = twitterData.flatMapToPair(f -> {
			TParserFinal tParser = objectMapper.readValue(f, TParserFinal.class);
			objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
			
			tParser.getEntities().getHashtags();
			String[] arr = tParser.getEntities().getHashtags().stream().map(x -> x.getText()).map(Object::toString).toArray(String[]::new);
			
			List<Tuple2<Tuple2<String, String>, Long>> ht = new ArrayList<>();
            for (int i=0; i<arr.length; i++) {
                for (int k=0; k<arr.length; k++) {
                    if (arr[i].compareTo(arr[k]) > 0 ) {
                        ht.add(new Tuple2<>(new Tuple2<>(arr[i], arr[k]), 1L));
                    }
                }
            }
            return ht.iterator();
		})
		
		.reduceByKey((x, y) -> x + y).mapToPair( x -> new Tuple2<>(x._2, x._1) ).sortByKey(false)
		.take(100)
		;
		
		   // Save file
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(outputFile), conf);
        FSDataOutputStream out = fs.create(new Path(outputFile));
        for (Tuple2<Long, Tuple2<String, String>> pair : pairs) {
            out.write((pair._1 + ",").getBytes(StandardCharsets.UTF_8));
            out.write((pair._2._1 + ",").getBytes(StandardCharsets.UTF_8));
            out.write((pair._2._2 + "").getBytes(StandardCharsets.UTF_8));
            out.write(("\n").getBytes(StandardCharsets.UTF_8));
        }
        out.close();

        System.out.println("Program_Log: Output written to file !");
		
		sc.stop();
		sc.close();
	}

}