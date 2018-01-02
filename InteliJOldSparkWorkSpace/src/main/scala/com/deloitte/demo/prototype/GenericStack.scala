package com.deloitte.demo.prototype

/**
  * Created by yuntliu on 10/31/2017.
  */
class GenericStack[A] {

  var elements: List[A]=Nil


  def push(x:A)={
    elements=x::elements
  }
  def peek():A={
    elements.head
  }

  def pop():A={
  val top=elements.head;
    elements=elements.tail
    return top
  }
  def printOut():Unit={
    for(x<-elements)
      {
        println(x)
      }
  }

}
object runStack {
  def main(args: Array[String]): Unit = {
    val stack = new GenericStack[Int]
    println("===============")
    stack.push(1);
    stack.push(2);
    stack.push(3);
    stack.push(4);
    stack.push(5);

    stack.printOut()
    println("===============")
    println("peek")
    println(stack.peek())
    println("---------------")
    stack.printOut()
    println("===============")
    println("pop")
    println(stack.pop())
    println("---------------")
    println(stack.printOut())
  }
}