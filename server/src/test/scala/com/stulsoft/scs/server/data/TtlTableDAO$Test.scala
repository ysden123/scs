package com.stulsoft.scs.server.data

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Unit tests for ttlTableDAO
  *
  * @author Yuriy Stul
  */
class TtlTableDAO$Test extends FlatSpec with Matchers with LazyLogging with ScalaFutures {
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
  behavior of "TtlTableDAO"

  "putTtl" should "add new ttl" in {
    Await.result(TtlTableDAO.putTtl(dbTest, Ttl("key1", 123, 1000L)), 2.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(dbTest, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 1000L))
  }

  it should "update existing ttl" in {
    Await.result(TtlTableDAO.putTtl(dbTest, Ttl("key1", 123, 1000L)), 2.seconds)
    Await.result(TtlTableDAO.putTtl(dbTest, Ttl("key1", 123, 2000L)), 2.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(dbTest, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 2000L))
  }

  "getTtl" should "get ttl" in {
    Await.result(TtlTableDAO.putTtl(dbTest, Ttl("key1", 123, 1000L)), 2.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(dbTest, "key1"), 2.seconds)
    ttl.isDefined should equal(true)
    ttl.get should equal(Ttl("key1", 123, 1000L))
    ttl.get.startDate should equal(1000L)
  }

  it should "return empty ttl for non-existing key" in {
    Await.result(TtlTableDAO.deleteTtl(dbTest, "key1"), 2.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(dbTest, "key1"), 2.seconds)
    ttl.isDefined should equal(false)
  }

  "delete" should "delete existing ttl" in {
    Await.result(TtlTableDAO.putTtl(dbTest, Ttl("key1", 123, 1000L)), 2.seconds)
    Await.result(TtlTableDAO.deleteTtl(dbTest, "key1"), 2.seconds)
    val ttl = Await.result(TtlTableDAO.getTtl(dbTest, "key1"), 2.seconds)
    ttl.isDefined should equal(false)
  }

  it should "work with non-existing ttl" in {
    Await.result(TtlTableDAO.deleteTtl(dbTest, "key1"), 2.seconds)
  }
}
