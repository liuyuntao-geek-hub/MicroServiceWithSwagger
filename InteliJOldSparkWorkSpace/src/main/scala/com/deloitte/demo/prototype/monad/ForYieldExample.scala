package com.deloitte.demo.prototype.monad

/**
  * Created by yuntliu on 11/8/2017.
  */

object ForYieldExample {
  def main(args: Array[String]): Unit = {

    val data = Range(1,10).toArray
    val data2 = Range(11,20).toArray
    println("use map function directly")
    data.map(a=>{a*2}).foreach(println)

    var counter=0;
    println("Option 1: use for yield function")
    val newdata3={
      for{
        newData<-data;
        newData2<-data2
      }
        yield{
          (newData2*2,newData)

        }}
    newdata3.foreach(println)

    println("Option 2: use flatMap and Map - exactly the same as Option 1 with much shorter code")
    data.flatMap(newData=>{data2.map(newData2=>{(newData2*2,newData)})}).foreach(println)

    // flatMap: generate sequence and the flat it
    println("Option 3: use closure function pass to flatmap and map with more flexibility")
    //  data.flatMap(a=>{Seq((a,{counter+1;return counter}),(a,{counter+1;return counter}),(a,{counter+1;return counter}))}).foreach(println)

    data.flatMap(a=>(Seq((a,1),(a,2),(a,3)))).map(x=>(x._1,x._2,x._1+x._2)).foreach(println)
  }
}

