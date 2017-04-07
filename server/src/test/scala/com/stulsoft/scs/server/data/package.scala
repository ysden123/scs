package com.stulsoft.scs.server

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
package object data extends LazyLogging{
  val dbTest: Database = Database.forConfig("h2mem1Test")
  private val setup = DBIO.seq(
    (dataTable.schema ++ ttlTable.schema).create
  )
  logger.info("Test database initialization started")
  Await.result(dbTest.run(setup), 2.seconds)
  logger.info("Test database initialization completed")
}
