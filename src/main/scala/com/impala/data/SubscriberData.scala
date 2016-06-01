package com.impala.data

import scalikejdbc.WrappedResultSet

/**
  * Created by yash.datta on 01/06/16.
  */
case class SubscriberAggregate(
  FLOW_DOWN_BYTES: Long,
  FLOW_UP_BYTES: Long,
  ROAMING_SESSION_COUNT: Long,
  SESSION_COUNT: Long,
  ROAMING_SESSION_DURATION: Long,
  TOTAL_SESSION_DURATION: Long,
  ROAMING_FLOW_UP_BYTES: Long,
  ROAMING_FLOW_DOWN_BYTES: Long)

object SubscriberAggregate {
  def apply(rs: WrappedResultSet): SubscriberAggregate  = SubscriberAggregate (
    FLOW_DOWN_BYTES = rs.long("FLOW_DOWN_BYTES"),
    FLOW_UP_BYTES = rs.long("FLOW_UP_BYTES"),
    ROAMING_SESSION_COUNT = rs.long("ROAMING_SESSION_COUNT"),
    SESSION_COUNT = rs.long("SESSION_COUNT"),
    ROAMING_SESSION_DURATION = rs.long("ROAMING_SESSION_DURATION"),
    TOTAL_SESSION_DURATION = rs.long("TOTAL_SESSION_DURATION"),
    ROAMING_FLOW_UP_BYTES = rs.long("ROAMING_FLOW_UP_BYTES"),
    ROAMING_FLOW_DOWN_BYTES = rs.long("ROAMING_FLOW_DOWN_BYTES")
  )
}