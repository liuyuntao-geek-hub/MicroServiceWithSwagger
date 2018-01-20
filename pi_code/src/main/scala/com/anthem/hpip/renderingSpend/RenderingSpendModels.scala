package com.anthem.hpip.renderingSpend

case class TINSXWLkVarianceSchema(tableName: String, partitionColumn: String, partitionValue: String,columnName: String,
                          operationDescription: String, operation: String, operationValue: Double,
                          percentageVariance: Double, threshold: Double,isThresholdCrossed:String,subjectArea:String)