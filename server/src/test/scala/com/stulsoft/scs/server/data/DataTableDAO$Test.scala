/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server.data

import com.stulsoft.scs.common.data.Data
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Unit tests for DataTableDAO
  *
  * @author Yuriy Stul
  */
class DataTableDAO$Test extends FlatSpec with Matchers with LazyLogging with ScalaFutures {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  behavior of "DataTableDAO"

  "putData" should "add new data" in {
    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 2.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value1", 0L))
  }

  it should "update existing data" in {
    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 2.seconds)
    Await.result(DataTableDAO.putData(db, "key1", "value2", 0L), 2.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value2", 0L))
  }

  "getData" should "return data" in {
    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 2.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(true)
    d.get should equal(Data("key1", "value1", 0L))
  }

  it should "return empty data for non-existing key" in {
    Await.result(DataTableDAO.deleteData(db, "key1"), 2.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(false)
  }

  "deleteData" should "delete existing data" in {
    Await.result(DataTableDAO.putData(db, "key1", "value1", 0L), 2.seconds)
    Await.result(DataTableDAO.deleteData(db, "key1"), 2.seconds)
    val d = Await.result(DataTableDAO.getData(db, "key1"), 2.seconds)
    d.isDefined should equal(false)
  }

  it should "work for non-existing data" in {
    Await.result(DataTableDAO.deleteData(db, "key1"), 2.seconds)
  }
}
