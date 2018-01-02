package com.deloitte.demo.prototype

import scala.util.Random

/**
  * Created by yuntliu on 11/5/2017.
  */
class PatternMatchCaseClass {

}
object PatternMatchCaseClass{
  def main(args: Array[String]): Unit = {

    val x = Random.nextInt(10)

    val y = x match {
      case 0 => "zero"
      case 1 => "one"
      case 2 => "two"
      case 3 => "three"
      case 4 => "four"
      case 5 => "five"
      case 6 => "six"
      case 7 => "seven"
      case 8 => "eight"
      case 9 => "nine"
      case 10=> "ten"
      case _ => "all others"
    }

    println(y)
  }



}