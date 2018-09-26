/*
 * Copyright (c) 2017, Anthem Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE THIS FILE HEADER.
 *
 */
package com.anthem.hca.spcp.config

/**
 * Keys used in -.conf, -.sql files.
 *
 */
object ConfigKey {

	// App/DB config
			val inboundSpliceDB: String = "inbound-splice-db"
			val auditColumnName: String = "audit-column-name"
			val spcpSpliceDB: String = "spcp-splice-db"
			val spcpSpliceDBPlaceHolder: String = "<spcp-splice-db>"

}
