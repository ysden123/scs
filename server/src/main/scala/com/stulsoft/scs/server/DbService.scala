/*
 * Copyright (c) 2017. Yuriy Stul
 */
package com.stulsoft.scs.server

import com.stulsoft.scs.server.data.Data
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

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
      Integer.valueOf(key)
      //      Some(Data(key, "the value", 0L))
      Option(Data(key, "the value", 0L))
    } catch {
      case _: Exception => None
    }
  }

  def put(data: Data): Future[Option[Boolean]] = Future {
    Option(true)
  }

  def delete(key: String): Future[Option[Boolean]] = ???
}
