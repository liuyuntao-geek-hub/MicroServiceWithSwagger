package com.deloitte.demo

import com.deloitte.demo.sasLogic.wordCountOperation
import org.apache.commons.httpclient.util.DateUtil
import org.scalatest.{BeforeAndAfter, FunSuite}
import com.deloitte.demo.util.DateUtil
import com.holdenkarau.spark.testing._

/**
  * Created by yuntliu on 12/4/2017.
  */
class AllOperationTest extends  FunSuite with BeforeAndAfter with SharedSparkContext {

  before{
    println("Testing Start")
    System.setProperty("hadoop.home.dir", "C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\libs\\winutils");
  }
  after{
    println("Testing Completed")
  }

  test("Spend - Happy Path Execution of the Operation") {
    (new wordCountOperation(AppName = "FirstTestApp", master = "local[2]"
    )).operation()
  }

  test("MTCLM - An empty Set should have size 0") {
    assert(Set.empty.size == 0)
    println("Test of the test case")
  }

  test("TinTarget - Test of a failure presentation") {
    assert(Set.empty.size == 0)
    println("Test of the test case")
    //  fail("Pretend to fail")
  }

  test("Pending to write") {
    (pending)
  }


  test("Rendering Spend - Spark Testing Base - sc dummy test"){

    println("Create Parallized Data Sets using sc and turn it off after completion of this test")
    val data = Range(1,1000001).toArray
    val rddData = sc.parallelize(data)
    // Now the data can be parallized for calculation
    // Range exclusive the top boundary

    val addAll=rddData.reduce((s1,s2)=>s1+s2)
    println (s"Range 1, 1000001 total: $addAll")
    var a = (1+1000000)*(1000000/2)
    println (s"Range 1, 1000001 calc value: $a")

  }


  test("Visit - Spark Testing Base - rerun sc dummy test: no conflicts") {
    val data = Range(0, 11).toArray
    println("Create Parallized Data Sets using sc and turn it off after completion of this test")
    val rddData = sc.parallelize(data, 10)
    // This will give the partition as 10
    // This will create 0 -> 10 = 11 numbers
    val getResult = rddData.reduce((a, b) => a + b)
    // This will take 2 numbers and add them together: 0+1=1 -> 1+2=3 -> 3+3=6 -> 6+4 =10 ...... = 55
    val getResultWithMinus = rddData.reduce((a, b) => a + b - 1)
    // This will take 2 numbers and add then -1 => totally do it 10 times => 45
    println(s"Get Result with Minus: $getResultWithMinus")

  }


}
