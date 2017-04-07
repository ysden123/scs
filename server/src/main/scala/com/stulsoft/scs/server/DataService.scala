/*
 * Copyright (c) 2017. Yuriy Stul
 */
package com.stulsoft.scs.server

import com.stulsoft.scs.common.data.Data
import com.stulsoft.scs.server.data.DataTableDAO
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Data service
  *
  * @author Yuriy Stul
  */
class DataService() extends LazyLogging{
  db = Database.forConfig("h2mem1")
  private val setup = DBIO.seq(
    (dataTable.schema ++ ttlTable.schema).create
  )

  logger.info("Database initialization started")
  Await.result(db.run(setup), 2.seconds)
  logger.info("Database initialization completed")

  import scala.concurrent.ExecutionContext.Implicits.global
  /**
    * Returns a data for specified key
    *
    * @param key specifies the data to find
    * @return the data for specified key
    */
  def getData(key: String): Future[Option[Data]] = Future {
    try {
      Await.result(DataTableDAO.getData(db, key), 2.seconds)
    } catch {
      case _: Exception => None
    }
  }

  /**
    * Stores a data into database
    *
    * @param data the data to store
    * @return result of saving
    */
  def putData(data: Data): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.putData(db, data.key, data.value, data.ttl), 2.seconds)
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }

  /**
    * Deletes a data with specified key
    *
    * @param key specifies the data to delete
    * @return result of deleting
    */
  def deleteData(key: String): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.deleteData(db, key), 2.seconds)
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }
}
