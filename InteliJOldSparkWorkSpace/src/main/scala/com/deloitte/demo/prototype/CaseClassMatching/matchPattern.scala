package com.anthem.padp.Prototype.CaseClassMatching

/**
  * Created by yuntliu on 11/5/2017.
  */
class matchPattern {

}

object matchPattern{
  def main(args: Array[String]): Unit = {
    val someSms = SMS("123", "Are you there")
    println(showNotification(someSms))

    val p = Phone("iPhone x")
    getDeviceInfo(p)
  }

  def showNotification (notification: Notification): String={

    notification match {
      case Email(email, mytitle, _) =>
      {"You got email from: " + email}
      case SMS(number, message) if number=="1235" =>
        s"You got SMS message from $number: $message"
      case _ => "you got other message"
    }
  }

  def getDeviceInfo (device: Device) = device match {
    case p:Phone => p.screenSaverOn
    case c:Computer=>c.screenSaverOff
    case _ => println("Other Device")
  }

}