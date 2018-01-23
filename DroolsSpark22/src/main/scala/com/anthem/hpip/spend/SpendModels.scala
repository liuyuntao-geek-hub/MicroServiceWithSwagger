package com.anthem.hpip.spend

case class VarianceSchema(tableName: String, partitionColumn: String, partitionValue: String,columnName: String,
                          operationDescription: String, operation: String, operationValue: Double,
                          percentageVariance: Double, threshold: Double,isThresholdCrossed:String,subjectArea:String)