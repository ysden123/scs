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
    println("==>testGet")
    val r = Await.result(client.getData("1"), 2.seconds)
    println(s"r: $r")
    println(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    println("<==testGet")
  }

  def testDelete(client: Client): Unit = {
    println("==>testDelete")
    val r = Await.result(client.deleteData("1"), 2.seconds)
    println(s"r: $r")
    println(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    println("<==testDelete")
  }

  def testPut(client: Client): Unit = {
    println("==>testPut")
    val r = Await.result(client.putData(Data("1", "value 11", 0)), 2.seconds)
    println(s"r: $r")
    println(s"status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    println("<==testPut")
  }

  def testTtl(client: Client): Unit = {
    println("==>testTtl")
    val key = "ttl1"
    var r = Await.result(client.putData(Data(key, "ttl value", 2000)), 2.seconds)
    r = Await.result(client.getData(key), 2.seconds)
    println(s"(1) r: $r")
    println(s"(1) status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    Thread.sleep(2100)
    r = Await.result(client.getData(key), 2.seconds)
    println(s"(2) r: $r")
    println(s"(2) status: ${r.status}, data: ${r.data}, error message: ${r.errorMessage}")
    println("<==testTtl")
  }

  try {
    testGet(client)
  } catch {
    case e: Exception => println(e.getMessage)
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
    case e: Exception => println(s"Exception: ${e.getMessage}")
  }
  finally client.close()
  logger.info("Stopped ClientTest")
}
