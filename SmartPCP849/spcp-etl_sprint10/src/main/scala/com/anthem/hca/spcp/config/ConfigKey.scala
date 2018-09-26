/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
 */
package com.anthem.hca.spcp.config

object ConfigKey {

  // App/DB config
  val auditColumnName: String = "audit-column-name"
  val spcpSpliceDB: String = "spcp-splice-db"
  val cdlEdwdDB: String = "cdl-edwd-db"
  val cdlPimsDB: String = "cdl-pims-db"
  val cdlWgspDB: String = "cdl-wgsp-db"

  val spcpSpliceDBPlaceHolder: String = "<spcp-splice-db>"   //dv_pdpspcp_xm or ts_pdps
  val cdlEdwdDBPlaceHolder: String = "<cdl-edwd-db>"
  val cdlPimsDBPlaceHolder: String = "<cdl-pims-db>"
  val cdlWgspDBPlaceHolder: String = "<cdl-wgsp-db>"
  
  val varianceTblPlaceHolder: String = "<table_name>"

}
