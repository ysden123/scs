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

  testPut(client)
  testGet(client)
  testDelete(client)
  testGet(client)

  logger.info("Stopped ClientTest")
}
