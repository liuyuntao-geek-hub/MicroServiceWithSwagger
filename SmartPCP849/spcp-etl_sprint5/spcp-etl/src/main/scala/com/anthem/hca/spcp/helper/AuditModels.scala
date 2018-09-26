package com.anthem.hca.spcp.helper

import java.sql.Timestamp

case class Audit(program: String, user_id: String, app_id: String, start_time: Timestamp, duration: String, status: String)

