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
  val cdlRfdmDB: String = "cdl-rfdm-db"
  
  val cdlEdwdAllphiDB: String = "cdl-edwd-allphi"
  val cdlPimsAllphiDB: String = "cdl-pims-allphi"
  val cdlWgspAllphiDB: String = "cdl-wgsp-allphi"
  val cdlRfdmAllphiDB: String = "cdl-rfdm-allphi"

  val spcpSpliceDBPlaceHolder: String = "<spcp-splice-db>"   
  val cdlEdwdDBPlaceHolder: String = "<cdl-edwd-db>"
  val cdlPimsDBPlaceHolder: String = "<cdl-pims-db>"
  val cdlWgspDBPlaceHolder: String = "<cdl-wgsp-db>"
  val cdlRfdmDBPlaceHolder: String = "<cdl-rfdm-db>"
  
  val spcpSpliceAllphiDBPlaceHolder: String = "<spcp-splice-allphi>"
  val cdlEdwdAllphiDBPlaceHolder: String = "<cdl-edwd-allphi>"
  val cdlPimsAllphiDBPlaceHolder: String = "<cdl-pims-allphi>"
  val cdlWgspAllphiDBPlaceHolder: String = "<cdl-wgsp-allphi>"
  val cdlRfdmAllphiDBPlaceHolder: String = "<cdl-rfdm-allphi>"
  
  val varianceTblPlaceHolder: String = "<table_name>"

}
