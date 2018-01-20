package com.anthem.hpip.spend

import java.sql.Timestamp

case class Spend(MCID: String, YEAR_MNTH_NBR: String, PMNT_TYPE: String,
                 QTR_NM: String, MBU: String,
                 FUNDCF: String, PRODCF: String, ALLOWED: String, PAID: String,
                 STRT_DT: String, END_DT: String, DRVD_DT: String)
                 
 case class Spend_D(MCID: String, MONTH: String, PMNT_TYPE: String,
                 QTR_NM: String, MBU: String,
                 FUNDCF: String, PRODCF: String, ALLOWED: String, PAID: String,
                 START: String, END: String)

case class CorrectedSpend(MCID: String, DRVD_DT: String, INPAT_Allowed: String, INPAT_Paid: String,
                          OUTPUT_Allowed: String, OUTPUT_Paid: String, CPTTN_ALLOWED: String, CPTTN_PAID: String,
                          RX_ALLOWED: String, RX_PAID: String, PROF_ALLOWED: String, PROF_PAID: String,
                          TRNSPLNT_ALLOWED: String,
                          TRNSPLNT_PAID:    String, Allowed_Total: String, Paid_Total: String)
                          
                          
case class VarianceSchema(tableName: String, partitionColumn: String, partitionValue: String,columnName: String,
                          operationDescription: String, operation: String, operationValue: Double,
                          percentageVariance: Double, threshold: Double,isThresholdCrossed:String)