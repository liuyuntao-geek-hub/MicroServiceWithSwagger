package com.deloitte.demo.prototype

/**
  * Created by yuntliu on 11/4/2017.
  */
class ImplicitOption {

  def printFunc (firstString: String)(implicit oneString:String):Unit={
    println(firstString + " ; " + oneString)
  }
  def printOption(oneString:Option[String]):Unit={
    println(oneString.getOrElse("Something Else"))
  }
}

object runOption{

  def main(args: Array[String]): Unit = {

    implicit val IM_String:String = "This is Implicit"

    (new ImplicitOption()).printFunc("Yes")
    (new ImplicitOption).printOption(None)
    (new ImplicitOption).printOption(Some("String"))


  }
}