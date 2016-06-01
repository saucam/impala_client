package com.impala.tasks

import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}

import com.google.common.util.concurrent.ThreadFactoryBuilder
import scalikejdbc.{DB, NoExtractor, ResultSetExtractorException, SQL, UnexpectedNullValueException, WrappedResultSet}

import scala.collection.immutable.HashMap
import scalaz.concurrent.Task

/**
  * Created by yash.datta on 01/06/16.
  */
/**
  * Basic Building Blocks for creating tasks for executing sql queries
  */
object TaskFactory {

  // Initialize thread pool for parallel query execution
  // We use a cached pool for the expected
  // numerous short-lived queries
  val clientThreadFactory: ThreadFactory = new ThreadFactoryBuilder()
    .setNameFormat("client-query-pool-thread-%d").build()

  val clientExecutorService: ExecutorService = {
    Executors.newCachedThreadPool(clientThreadFactory)
  }

  /**
    * * Returns Scalaz's Task abstraction over given sql
    *
    * @param resultFunc - used to get output in the form of desired type.
    * @param query - query to be executed
    * @tparam A - Desired output type
    * @return - Scalaz's Task
    */
  def getTaskSingleResult[A](resultFunc: WrappedResultSet => A, query: SQL[A, NoExtractor]): Task[Option[A]] = {
    getTask {
      try {
        DB readOnly { implicit session =>
          query.map(rs => resultFunc(rs)).single.apply
        }
      } catch {
        case e: UnexpectedNullValueException =>
          None
        case e: ResultSetExtractorException if (e.message.contains("Failed to retrieve value because null")) =>
          None
      }
    }
  }

  def getTaskListResult[A](resultFunc: WrappedResultSet => A, query: SQL[A, NoExtractor]): Task[List[A]] = {
    getTask {
      try {
        DB readOnly { implicit session =>
          query.map(rs => resultFunc(rs)).list.apply
        }
      } catch {
        case e: UnexpectedNullValueException =>
          List[A]()
        case e: ResultSetExtractorException if (e.message.contains("Failed to retrieve value because null")) =>
          List[A]()
      }
    }
  }

  def getTaskMapResult[K, V](resultFunc: WrappedResultSet => (K, V), query: SQL[(K, V), NoExtractor]): Task[Map[K, V]] = {
    getTask {
      try {
        HashMap() ++
          (DB readOnly { implicit session =>
            query.map(rs => resultFunc(rs)).list.apply
          })
      } catch {
        case e: UnexpectedNullValueException =>
          HashMap()
        case e: ResultSetExtractorException if (e.message.contains("Failed to retrieve value because null")) =>
          HashMap()
      }
    }
  }

  def getTaskFirstResult[A](resultFunc: WrappedResultSet => A, query: SQL[A, NoExtractor]): Task[Option[A]] = {
    // ...
    getTask {
      try {
        DB readOnly { implicit session =>
          query.map(rs => resultFunc(rs)).first.apply
        }
      } catch {
        case e: UnexpectedNullValueException =>
          None
        case e: ResultSetExtractorException if (e.message.contains("Failed to retrieve value because null")) =>
          None
      }
    }
  }

  /**
    * Returns the task by wrapping the given closure computation in a task
    * Adds careExecutorService as the pool to be used for executing this task
    *
    * @param a - Computation to be performed by the task
    * @param execService - Executor Thread Pool to be used for executing this task
    * @tparam A - Desired Ouput Type
    * @return - Scalaz's Task
    */
  def getTask[A](a: => A)(implicit execService: ExecutorService = clientExecutorService): Task[A] = Task(a)(execService)

}

