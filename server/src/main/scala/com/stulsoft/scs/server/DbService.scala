/*
 * Copyright (c) 2017. Yuriy Stul
 */
package com.stulsoft.scs.server

import com.stulsoft.scs.common.data.Data
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Database service
  *
  * @author Yuriy Stul
  */
class DbService() extends LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val db = Database.forConfig("h2mem1")
  private val setup = DBIO.seq(
    dataTable.schema.create
  )

  logger.info("Database initialization started")
  Await.result(db.run(setup), 2.seconds)
  logger.info("Database initialization completed")

  def get(key: String): Future[Option[Data]] = Future {
    try {
      Await.result(DataTableDAO.getData(db, key), 2.seconds)
    } catch {
      case _: Exception => None
    }
  }

  def put(data: Data): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.putData(db, data.key, data.value, data.ttl), 2.seconds)
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }

  def delete(key: String): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.deleteData(db, key), 2.seconds)
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }
}
