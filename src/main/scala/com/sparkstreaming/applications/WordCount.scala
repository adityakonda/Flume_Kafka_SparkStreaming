package com.sparkstreaming.applications

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by aditya on 01/08/2018.
  */
object WordCount {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("yarn-client")
    conf.setAppName("Streaming Word Count")
    val ssc = new StreamingContext(conf,Seconds(10))

    val lines = ssc.socketTextStream("sandbox.hortonworks.com",9999)

    val words = lines.flatMap(line => line.split(" "))
    val wordCount = words.map( word => (word,1)).reduceByKey((k,v) => k + v)

    wordCount.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
