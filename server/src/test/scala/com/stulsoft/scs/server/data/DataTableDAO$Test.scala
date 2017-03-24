/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server.data

import com.stulsoft.scs.server.dataTable
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Unit tests for DataTableDAO
  *
  * @author Yuriy Stul
  */
class DataTableDAO$Test extends FlatSpec with BeforeAndAfter with Matchers with LazyLogging with ScalaFutures {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  var db: Database = _

  private def createSchema() =
    db.run(dataTable.schema.create).futureValue

  before {
    db = Database.forConfig("h2mem1")
  }

  behavior of "DataTableDAO"

  "putData" should "add new data" in {
    createSchema()

    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 20.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value1", 0L))
  }

  it should "update existing data" in {
    createSchema()

    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 20.seconds)
    Await.result(DataTableDAO.putData(db, "key1", "value2", 0L), 20.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value2", 0L))
  }

  "getData" should "return data" in {
    createSchema()

    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 20.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 20.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value1", 0L))
  }

  it should "return empty data for non-existing key" in {
    createSchema()

    val d = Await.result(DataTableDAO.getData(db, "key1"), 20.seconds)
    d.isDefined should equal(false)
  }

  after {
    db.close
  }
}
