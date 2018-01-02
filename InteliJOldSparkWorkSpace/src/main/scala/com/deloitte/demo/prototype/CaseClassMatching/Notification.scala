package com.anthem.padp.Prototype.CaseClassMatching

/**
  * Created by yuntliu on 11/5/2017.
  */
abstract class Notification {

}

case class Email(sender:String, title:String, body:String) extends Notification
case class SMS (caller:String, message:String) extends Notification
case class VoiceRecording (contactName:String, link:String) extends Notification

abstract class Device
{

}

case class Phone (model: String) extends Device {
  def screenSaverOn = println ("Turn on Screen Saver for Phone ")
}

case class Computer (model: String) extends Device {
  def screenSaverOff:String = "Turn off Screen Saver for this computer"
}

