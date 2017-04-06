package com.stulsoft.scs.server.data

import com.stulsoft.scs.server.dataTable
import com.stulsoft.scs.server.ttlTable
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Unit tests for ttlTableDAO
  *
  * @author Yuriy Stul
  */
class TtlTableDAO$Test extends FlatSpec with BeforeAndAfter with Matchers with LazyLogging with ScalaFutures {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  var db: Database = _

  private def createSchema() =
    db.run(ttlTable.schema.create).futureValue

  before {
    db = Database.forConfig("h2mem1")
  }

  behavior of "TtlTableDAO"

  "putTtl" should "add new ttl" in {
    createSchema()

    Await.result(TtlTableDAO.putTtl(db, Ttl("key1", 123, 1000L)), 20.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(db, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 1000L))
  }

  it should "update existing ttl" in {
    createSchema()

    Await.result(TtlTableDAO.putTtl(db, Ttl("key1", 123, 1000L)), 20.seconds)
    Await.result(TtlTableDAO.putTtl(db, Ttl("key1", 123, 2000L)), 20.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(db, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 2000L))
  }

  "getTtl" should "get ttl" in {
    createSchema()

    Await.result(TtlTableDAO.putTtl(db, Ttl("key1", 123, 1000L)), 20.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(db, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 1000L))
  }

  it should "return empty ttl for non-existing key" in {
    createSchema()

    val ttl = Await.result(TtlTableDAO.getTtl(db, "key1"), 2.seconds)
    ttl.isDefined should equal(false)
  }

  "delete" should "delete existing ttl" in {
    createSchema()

    Await.result(TtlTableDAO.putTtl(db, Ttl("key1", 123, 1000L)), 20.seconds)
    Await.result(TtlTableDAO.deleteTtl(db, "key1"), 20.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(db, "key1"), 2.seconds)
    ttl.isDefined should equal(false)
  }

  it should "work with non-existing ttl" in {
    createSchema()

    Await.result(TtlTableDAO.deleteTtl(db, "key1"), 20.seconds)
  }

  after {
    db.close
  }
}
