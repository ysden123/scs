/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.io.StdIn

/**
  * SCS server
  *
  * @author Yuriy Stul
  */
object Server extends App with LazyLogging {
  implicit val system = ActorSystem("scs-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val service = new Service
  private val bindingFuture = Http().bindAndHandle(service.route, "localhost", 8080)
  logger.info(s"SCS server started at http://localhost:8080/v.0.0")

  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete { _ =>
    logger.info("SCS stopped")
    system.terminate()
  } // and shutdown when done
}
