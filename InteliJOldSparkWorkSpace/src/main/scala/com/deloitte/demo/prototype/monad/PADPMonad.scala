package com.deloitte.demo.prototype.monad
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
/**
  * Created by yuntliu on 10/30/2017.
  */
trait PADPMonad[A] { current: PADPMonad[A] =>
  def run(sqlContext: SQLContext) : A

  def flatMap[B] (f: A => PADPMonad[B]): PADPMonad[B] =
    new PADPMonad[B] {
      override def run(sqlContext: SQLContext): B = {
        val previousResult: A = current.run(sqlContext)
        val nextMonad: PADPMonad[B] = f(previousResult)
        println("I am Flat")
        nextMonad.run(sqlContext)
      }
    }

  def map[B] (f: A => B) : PADPMonad[B] =
    new PADPMonad[B] {
      override def run(sqlContext: SQLContext): B = {
        println("I am map")
        f(current.run(sqlContext))
      }
    }
}

