package com.deloitte.demo.prototype

/**
  * Created by yuntliu on 11/4/2017.
  */
class FlattenFunction{

}
object FlattenFunction {

  def main(args: Array[String]): Unit = {

    val data = Range(1,10).toArray
    val data2 = Range(11,20).toArray
    val data3 = Range(21,25).toArray

    val seq1 = Seq(data,data2,data3).flatten.filter(a=>a>=5).foreach(println)


  }

}
