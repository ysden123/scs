package com.stulsoft.scs.server

import akka.actor.{ActorRef, ActorSystem}
import com.stulsoft.scs.server.actor.CheckTtl
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

/**
  * TTL service
  *
  * @author Yuriy Stul
  */
class TtlService(val dbActor: ActorRef) extends LazyLogging {
  require(dbActor != null, "dbActor should be defined")
  implicit val system = ActorSystem("scs-server-ttl-system")
  //Use the system's dispatcher as ExecutionContext
  import system.dispatcher

  def start(): Unit = {
    logger.info("TTL Service started")
    system.scheduler.schedule(2.seconds, 1.seconds) {
      checkTtl()
    }
  }

  private def checkTtl(): Unit = {
    dbActor ! CheckTtl
  }

  def stop(): Unit = {
    system.terminate()
    logger.info("TTL Service stopped")
  }
}
