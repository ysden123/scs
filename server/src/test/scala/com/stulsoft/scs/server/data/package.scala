package com.stulsoft.scs.server

import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
package object data {
  val db: Database = Database.forConfig("h2mem1")
  private val setup = DBIO.seq(
    (dataTable.schema ++ ttlTable.schema).create
  )

  Await.result(db.run(setup), 2.seconds)
}
