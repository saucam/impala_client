package com.impala.client

import com.impala.client.conf.ConnConf
import com.impala.data._
import com.impala.tasks.TaskFactory._

import scalikejdbc._

import scalaz.concurrent.Task

/**
  * Created by yash.datta on 31/05/16.
  */
object Main {

  def executeParallelTasks(): Unit = {
    val t1: Task[Option[SubscriberAggregate]] = getTaskSingleResult(
      SubscriberAggregate.apply,
      sql"""select sum(flow_up_bytes) as flow_up_bytes,
            |sum(flow_down_bytes) as flow_down_bytes,
            |sum(roaming_session_count) as roaming_session_count,
            |sum(session_count) as session_count,
            |sum(roaming_session_duration) as roaming_session_duration,
            |sum(total_session_duration) as total_session_duration,
            |sum(roaming_flow_down_bytes) as roaming_flow_down_bytes,
            |sum(roaming_flow_up_bytes) as roaming_flow_up_bytes
            |from datagen3 where `timestamp` > 1460678400
            |and `timestamp` <= 1460682000 group by networkelement limit 1""".stripMargin
    )


    val t2 = getTaskListResult(
      SubscriberAggregate.apply,
      sql"""select sum(flow_up_bytes) as flow_up_bytes,
            |sum(flow_down_bytes) as flow_down_bytes,
            |sum(roaming_session_count) as roaming_session_count,
            |sum(session_count) as session_count,
            |sum(roaming_session_duration) as roaming_session_duration,
            |sum(total_session_duration) as total_session_duration,
            |sum(roaming_flow_down_bytes) as roaming_flow_down_bytes,
            |sum(roaming_flow_up_bytes) as roaming_flow_up_bytes from datagen2
            |where `timestamp` > 1460678400 and `timestamp` <= 1460682000
            |group by msisdn limit 20""".stripMargin
    )

    print(Task.gatherUnordered(Seq(t1, t2)).run)
  }

  def main(args: Array[String]): Unit = {

    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = true,
      singleLineMode = false,
      printUnprocessedStackTrace = false,
      stackTraceDepth= 15,
      logLevel = 'debug,
      warningEnabled = false,
      warningThresholdMillis = 3000L,
      warningLogLevel = 'warn
    )

    ConnConf

    val t1: Task[Option[SubscriberAggregate]] = getTaskSingleResult(
      SubscriberAggregate.apply,
      sql"""select sum(flow_up_bytes) as flow_up_bytes,
            |sum(flow_down_bytes) as flow_down_bytes,
            |sum(roaming_session_count) as roaming_session_count,
            |sum(session_count) as session_count,
            |sum(roaming_session_duration) as roaming_session_duration,
            |sum(total_session_duration) as total_session_duration,
            |sum(roaming_flow_down_bytes) as roaming_flow_down_bytes,
            |sum(roaming_flow_up_bytes) as roaming_flow_up_bytes
            |from datagen3 where `timestamp` > 1460678400
            |and `timestamp` <= 1460682000 group by networkelement limit 1""".stripMargin
    )


    println(t1.unsafePerformSync)

  }
}
