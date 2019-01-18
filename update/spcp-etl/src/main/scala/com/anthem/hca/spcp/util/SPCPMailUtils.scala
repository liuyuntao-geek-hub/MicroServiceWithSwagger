package com.anthem.hca.spcp.util

import java.io.File
import java.util.Date
import java.util.Properties

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import grizzled.slf4j.Logging
import javax.mail.Address
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import grizzled.slf4j.Logging
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.anthem.hca.spcp.config.ConfigKey

object SpcpMailUtils extends Logging {

  def sendMail(config: Config, df: DataFrame) = {

   // val mailSender = config.getString("spcp.variance.sender")
    
    val recipientList = config.getString("spcp.variance.recipient.list")
    val strMailSubj = config.getString("spcp.variance.notification.subject")
    .replace(ConfigKey.varianceTblPlaceHolder, 
        df.select(df.col("table_nm")).head().getString(0))
    val strMailBody = config.getString("spcp.variance.notification.body")

   // info(s"[SPCP-ETL] mailsender is : $mailSender")
    info(s"[SPCP-ETL] recipientList is : $recipientList")
    info(s"[SPCP-ETL] mail subject is : $strMailSubj")
    info(s"[SPCP-ETL] mail body is : $strMailBody")

    val mailBody = strMailBody.concat(createHtmlEmailBody(df))

    val session = getMailSession(config)

    var toMailId: String = recipientList

    try {
      var msg: MimeMessage = new MimeMessage(session)
      msg.addRecipients(Message.RecipientType.TO, toMailId)
      msg.setHeader("Content-Type", "text/html")
      msg.setSubject(strMailSubj)
      msg.setSentDate(new Date())
      msg.setContent(mailBody, "text/html")

      Transport.send(msg)

    } catch {
      case me: MessagingException => {
        me.printStackTrace()
        info("<---Error in method sendScalaMail--->" + me)
      }
    }
  }

  def createHtmlEmailBody(df: DataFrame): String = {
    val htmlHeader = "<!DOCTYPE html>\n<html>\n<head>\n" + "<style type='text/css'>\n" + "table{ border: 3px solid black;font-family: Arial, Helvetica, sans-serif;border-collapse: collapse;}\n" + "td { border: 0.8px solid black;padding: 3px;font-size: 12px;}\n" + "th {background-color: #4CAF50;color: white;padding: 5px;border: 3px solid black;}\n" + "</style></head>\n" + "<body><div style='overflow-x:auto;'>"
    val columnNames = df.columns.mkString("</th><th>")

    var body: StringBuilder = new StringBuilder
    body.append(htmlHeader + "\n")
    body.append("<table style='width:40%'>" + "\n")
    body.append("<tr><th>" + columnNames.trim + "</th></tr>\n")

    df.collect().map(row => {
      body.append("<tr><td>" + row.mkString("</td><td>").trim + "</td></tr>\n")
    })

    body.append("</table></div></body></html>")
    body.toString
  }

  def getMailSession(config: Config): Session = {
    Session.getInstance(getSystemProperties(config), new MailAuthenticator(config))
  }

  private class MailAuthenticator(config: Config) extends Authenticator {
    override protected def getPasswordAuthentication: PasswordAuthentication = {
      val userName = config.getString("spcp.variance.mail.username")
      val password = config.getString("spcp.variance.mail.password")

      info(s"[SPCP-ETL] username is : $userName")
      new PasswordAuthentication(userName, password)
    }
  }

  private def getSystemProperties(config: Config): Properties = {
    val props: Properties = new Properties()
    val smtpAuth = config.getString("mail.smtp.auth")
    val smtpStartTLSEnable = config.getString("mail.smtp.starttls.enable")
    val smtpHost = config.getString("mail.smtp.host")
    val smtpPort = config.getString("mail.smtp.port")

    info(s"[SPCP-ETL] mail.smtp.auth is : $smtpAuth")
    info(s"[SPCP-ETL] mail.smtp.starttls.enable is : $smtpStartTLSEnable")
    info(s"[SPCP-ETL] mail.smtp.host is : $smtpHost")
    info(s"[SPCP-ETL] mail.smtp.port is : $smtpPort")

    props.setProperty("mail.smtp.auth", smtpAuth)
    props.setProperty("mail.smtp.starttls.enable", smtpStartTLSEnable)
    props.setProperty("mail.smtp.host", smtpHost)
    props.setProperty("mail.smtp.port", smtpPort)

    props
  }

//  def sendCntNtfctnMail(tableName: String, finalTargetDF: DataFrame, minRowCnt: Integer, maxRowCnt: Integer, spark: SparkSession, config: Config): DataFrame = {
//    
//    val finalDfCnt = finalTargetDF.count()  
//    if ((finalDfCnt < minRowCnt) || (finalDfCnt > maxRowCnt)) {
//      info("Member data not within threshold limits")
//      val mailDF = spark.sparkContext.parallelize(List((tableName, minRowCnt, maxRowCnt, finalDfCnt)))
//        .toDF("Table_Nm", "Min_Cnt", "Max_Cnt", "Current_Record_Cnt")
//      mailDF.show()
//      sendMail(config, mailDF)
//      null
//    } else {
//      finalTargetDF
//    }
//    
//  }
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession.builder.appName("test").master("local[2]").getOrCreate()
    import spark.implicits._
    val df = Seq(("hi", "hello")).toDF("column1", "column2")
    df.show

    val confPath = "C:\\Users\\af30986\\git-july\\spcp-etl\\src\\main\\resources\\spcp_etl_application_dev.properties"
    val config = ConfigFactory.parseFile(new File(confPath))

    sendMail(config, df)

  }

}