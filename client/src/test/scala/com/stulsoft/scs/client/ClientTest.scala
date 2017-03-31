package com.stulsoft.scs.client

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
object ClientTest extends App with LazyLogging {
  logger.info("Started ClientTest")
  val client = new Client()

  val r1 = Await.result(client.getData("1"), 2.seconds)
  println(s"r1: $r1")
  println(s"status: ${r1.status}, data: ${r1.data}, error message: ${r1.errorMessage}")
  logger.info("Stopped ClientTest")
}
