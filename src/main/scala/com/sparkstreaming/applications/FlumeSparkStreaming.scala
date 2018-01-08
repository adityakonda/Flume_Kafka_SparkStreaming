package com.sparkstreaming.applications

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume._
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by aditya on 12/18/2017.
  */
object FlumeSparkStreaming {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("yarn-client")
    conf.setAppName("flume Streaming application")

    val ssc = new StreamingContext(conf,Seconds(10))
    val flumeStream = FlumeUtils.createPollingStream(ssc,"sandbox-hdp.hortonworks.com",43223)
    val data = flumeStream.map(s => new String(s.event.getBody.array()))

    val departmentData = data.filter( msg =>
    {
      val endpoint = msg.split(" ")(6)
      endpoint.split("/")(1) == "department"
    })

    val departments = departmentData.map(rec => {

      val endpoint = rec.split(" ")(6)
      (endpoint.split("/")(2),1)
    })
    val departmentTrafficCapture = departments.reduceByKey((k,v) => k + v)
    departmentTrafficCapture.saveAsTextFiles("/user/root/departmentwiseTraffic/cnt")

    ssc.start()
    ssc.awaitTermination()
  }
}
