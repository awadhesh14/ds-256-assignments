package in.ds256.Assignment0;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.ds256.tparserfinal.TParserFinal;
import scala.Tuple2;
/**
 * DS-256 Assignment 0
 * Code for generating interaction graph
 */
public class InterGraph  {
	
	public static void main(String[] args) throws IOException {	
		
		String inputFile = args[0]; // Should be some file on HDFS
		String vertexFile = args[1]; // Should be some file on HDFS
		String edgeFile = args[2]; // Should be some file on HDFS
		
		SparkConf sparkConf = new SparkConf().setAppName("InterGraph");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);

		/**
		 * Code goes here
	 	 */
		ObjectMapper objectMapper = new ObjectMapper();
		 // Open file
        JavaRDD<String> twitterData = sc.textFile(inputFile).persist(StorageLevel.MEMORY_AND_DISK());
        System.out.println("Program_Log: File Opened !");
        
     // Get vertex info
        JavaRDD<String> vertexInfo = twitterData
        		
        		//.flatMapToPair((PairFlatMapFunction<String, Tuple2<Long, Long>, String>) InterGraph::getVertex)
        		.flatMapToPair(f -> {
        			TParserFinal tParser = objectMapper.readValue(f, TParserFinal.class);
        			objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss xxxx yyyy");
        			
        			Long id = tParser.getUser().getId();
        			if (id == null)
                        return Collections.emptyIterator();
        			Long created_at = tParser.getCreatedAt() == null ? 0L : ZonedDateTime.parse((String) tParser.getCreatedAt(), dtf).toInstant().toEpochMilli();
        			Long ts = ZonedDateTime.parse((String) tParser.getCreatedAt(), dtf).toInstant().toEpochMilli();
                    Long foc = (Long) tParser.getUser().getFollowersCount();
                    Long frc = (Long) tParser.getUser().getFriendsCount();
                    return Collections.singletonList(new Tuple2<>(new Tuple2<>(id, created_at), ts + "," + foc + "," + frc)).iterator();
        				
        		})
        		.reduceByKey((x, y) -> x + "," + y)
        		.map(x -> x._1._1 + "," + x._1._2 + "," + x._2)
        		;
        System.out.println("Program_Log: Obtained Vertex Info !");
        
        JavaRDD<String> edgeInfo = twitterData
        		.flatMapToPair(f -> {
        			TParserFinal tParser = objectMapper.readValue(f, TParserFinal.class);
        			objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss xxxx yyyy");
        			
        			Long src_id = tParser.getUser().getId();
        			Long sink_id = tParser.getRetweetedStatus().getUser().getId();
        			if (src_id == null || sink_id == null)
                        return Collections.emptyIterator();
        			Long originalTid = tParser.getRetweetedStatus().getId();
        			Long retweetTid = tParser.getId();
        			Long ts = ZonedDateTime.parse((String) tParser.getCreatedAt(), dtf).toInstant().toEpochMilli();
        			String hashtags = tParser.getEntities().getHashtags().stream().map(x -> x.getText()).map(Object::toString).collect(Collectors.joining(",")).toString();
        			if (hashtags.length() == 0)
                        return Collections.singletonList(new Tuple2<>(new Tuple2<>(src_id, sink_id), ts + "," + originalTid + "," + retweetTid )).iterator();
        			return Collections.singletonList(new Tuple2<>(new Tuple2<>(src_id, sink_id), ts + "," + originalTid + "," + retweetTid + "," + hashtags)).iterator();
        		})
        		.reduceByKey((x, y) -> x + ";" + y)
        		.map(x -> x._1._1 + "," + x._1._2 + ";" + x._2);
        System.out.println("Program_Log: Obtained Vertex Info !");
        
     // Save File
        vertexInfo.coalesce(1,true).saveAsTextFile(vertexFile);
        edgeInfo.coalesce(1,true).saveAsTextFile(edgeFile);
        
		sc.stop();
		sc.close();
	}

}