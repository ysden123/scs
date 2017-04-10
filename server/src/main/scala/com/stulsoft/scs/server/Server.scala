/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.stulsoft.scs.common.data._
import com.stulsoft.scs.server.actor.DBActor
import com.typesafe.scalalogging.LazyLogging

import scala.io.StdIn

/**
  * SCS server
  *
  * @author Yuriy Stul
  */
object Server extends App with LazyLogging {
  implicit val system = ActorSystem("scs-server-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val service = new Service(system.actorOf(Props[DBActor]))
  private val ttlService = new TtlService
  ttlService.start()

  private val bindingFuture = Http().bindAndHandle(service.route, "localhost", 8080)
  logger.info(s"SCS server started at http://localhost:8080/$version")

  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete { _ =>
    ttlService.stop()
    system.terminate()
    logger.info("SCS stopped")
  } // and shutdown when done
}
