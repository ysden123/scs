/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server.data

import com.stulsoft.scs.server.dataTable
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import slick.jdbc.H2Profile.api._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
class DataTableDAO$Test extends FlatSpec with BeforeAndAfter with Matchers with LazyLogging with ScalaFutures {
  var db: Database = _

  def createSchema() =
    db.run(dataTable.schema.create).futureValue

  before {
    logger.info("Initializing database started")
    db = Database.forConfig("h2mem1")
    logger.info("Initializing database completed")
  }

  behavior of "DataTableDAO"

  "putData" should "add new data" in {
    createSchema()

    DataTableDAO.putData(db, "key1", "value1", 0L)
    val d = DataTableDAO.getData(db, "key1")
    println(d)
  }

  it should "update existing data" in {
    createSchema()
  }

  after {
    logger.info("Stopping database started")
    db.close
    logger.info("Stopping database completed")
  }
}
