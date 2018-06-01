package com.anthem.hpip.targettin

case class VarianceSchemaa(tableName: String, partitionColumn: String, partitionValue: String,columnName: String,
                          operationDescription: String, operation: String, operationValue: Double,
                          percentageVariance: Double, threshold: Double,isThresholdCrossed:String,subjectArea:String)
 case class TinRecord(TIN:String,TARGETED_GROUP_OLD:String,TARGET_DATE:String,Program:String)
                          