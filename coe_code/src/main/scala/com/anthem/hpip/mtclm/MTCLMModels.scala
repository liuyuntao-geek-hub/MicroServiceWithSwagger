package com.anthem.hpip.mtclm

case class MTCLMVarianceSchema(tableName: String, partitionColumn: String, partitionValue: String,columnName: String,
                          operationDescription: String, operation: String, operationValue: Double,
                          percentageVariance: Double, threshold: Double,isThresholdCrossed:String)