package com.stulsoft.scs.server

import akka.actor.ActorSystem
import com.stulsoft.scs.server.data.{DataTableDAO, TtlTableDAO}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * TTL service
  *
  * @author Yuriy Stul
  */
class TtlService extends LazyLogging {
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
    Await.result(TtlTableDAO.getExpiredTtls(db), 2.seconds).foreach(ttl => {
      DataTableDAO.deleteData(db, ttl.key)
      TtlTableDAO.deleteTtl(db, ttl.key)
    })
  }

  def stop(): Unit = {
    system.terminate()
    logger.info("TTL Service stopped")
  }
}
