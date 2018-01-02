package com.deloitte.demo.prototype.configurationArtifactory

/**
  * Created by yuntliu on 11/8/2017.
  */

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
/**
  * Created by yuntliu on 10/23/2017.
  */
object ConfigurationFactory {
  def main(args: Array[String]): Unit = {

    /*
        1 - In reference.conf
          - Define all the default value
          - Could be override later by application.conf/json/properites
          = support both json or properties format
          = Support nested configuration as rootJson could be config
        2 - application.conf/json/properties will replace reference.conf if there is duplicates
        */
    var  conf:Config = ConfigFactory.load();
    var bar1:Int = conf.getInt("rootProperty.foo");
    println(bar1)
    var rootJson:Config = conf.getConfig("rootJson");
    var bar2:Int = rootJson.getInt("bar");
    println(bar2)
    var bar3:Int = conf.getInt("AppJson.foo")
    println(bar3)
    var myConfig:Config = ConfigFactory.load("customized");
    var bar5:Int = myConfig.getInt("AppJson.foo")
    println(bar5)
    var bar6:Int = conf.getInt("AppJson.foo")
    println(bar6)


  }

}

