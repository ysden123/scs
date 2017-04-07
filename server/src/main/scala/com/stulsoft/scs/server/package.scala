/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs

import com.stulsoft.scs.server.data.{DataTable, TtlTable}
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
package object server extends LazyLogging {
  val dataTable: TableQuery[DataTable] = TableQuery[DataTable]
  val ttlTable: TableQuery[TtlTable] = TableQuery[TtlTable]
  val db: Database = Database.forConfig("h2mem1")
  private val setup = DBIO.seq(
    (dataTable.schema ++ ttlTable.schema).create
  )

  logger.info("Database initialization started")
  Await.result(db.run(setup), 2.seconds)
  logger.info("Database initialization completed")

}
