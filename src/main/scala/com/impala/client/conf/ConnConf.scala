package com.impala.client.conf

/**
  * Created by yash.datta on 31/05/16.
  */
object ConnConf {

  // Get the configured dbname
  val dbName = scalikejdbc.config.DBs.config.getConfig("db.default").getString("dbName")
  val connUrl = scalikejdbc.config.DBs.config.getConfig("db.default").getString("url")

  // Initialize the connection pool
  scalikejdbc.config.DBs.setupAll

}
