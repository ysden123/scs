/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
object Server extends App with LazyLogging {
  val db = Database.forConfig("h2mem1")

  logger.info("SCS started")

  private def init(db: Database): Unit = {
    logger.info("Database initialization started")
    val setup = DBIO.seq(
      dataTable.schema.create
    )

    Await.result(db.run(setup), 2.seconds)
    logger.info("Database initialization completed")
  }

  try {
    init(db)
  } finally
    db.close()

  logger.info("SCS stopped")
}
