package com.sparkstreaming.applications

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by aditya on 01/08/2018.
  */
object DepartmentTraffic {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("yarn-client")
    conf.setAppName("Streaming Department Traffic")

    val ssc = new StreamingContext(conf,Seconds(30))
    val data = ssc.socketTextStream("sandbox.hortonworks.com",9876)

    data.print()

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