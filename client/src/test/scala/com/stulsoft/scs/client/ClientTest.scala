package com.stulsoft.scs.client

import com.stulsoft.scs.common.data.Data
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
object ClientTest extends App with LazyLogging {
  logger.info("Started ClientTest")
  lazy val client = Client("localhost", 8080)

  def testGet(client: Client): Unit = {
    logger.info("==>testGet")
    val r = Await.result(client.getData("1"), 2.seconds)
    logger.info(s"r: $r")
    logger.info(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    logger.info("<==testGet")
  }

  def testDelete(client: Client): Unit = {
    logger.info("==>testDelete")
    val r = Await.result(client.deleteData("1"), 2.seconds)
    logger.info(s"r: $r")
    logger.info(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    logger.info("<==testDelete")
  }

  def testPut(client: Client): Unit = {
    logger.info("==>testPut")
    val r = Await.result(client.putData(Data("1", "value 11", 0)), 2.seconds)
    logger.info(s"r: $r")
    logger.info(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    logger.info("<==testPut")
  }

  def testTtl(client: Client): Unit = {
    logger.info("==>testTtl")
    val key = "ttl1"
    var r = Await.result(client.putData(Data(key, "ttl value", 2000)), 2.seconds)
    r = Await.result(client.getData(key), 2.seconds)
    logger.info(s"(1) r: $r")
    logger.info(s"(1) status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    Thread.sleep(2100)
    r = Await.result(client.getData(key), 2.seconds)
    logger.info(s"(2) r: $r")
    logger.info(s"(2) status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    logger.info("<==testTtl")
  }

  try {
    testGet(client)
  } catch {
    case e: Exception => logger.info(e.getMessage)
  }

  client.open()
  try {
    testPut(client)
    testGet(client)
    testDelete(client)
    testGet(client)
    testTtl(client)
  }
  catch {
    case e: Exception => logger.info(s"Exception: ${e.getMessage}")
  }
  finally client.close()
  logger.info("Stopped ClientTest")
}
