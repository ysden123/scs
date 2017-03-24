/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/*
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
*/

/**
  * @author Yuriy Stul
  */
object Server extends App with LazyLogging {
  implicit val system = ActorSystem("scs-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val version = "v.0.0"
  val routes = path(version) {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Get</h1>"))
    } ~
      put {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Put</h1>"))
      } ~
      delete {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Delete</h1>"))
      }
  }
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  logger.info(s"SCS started at http://localhost:8080/$version")
  /*val db = Database.forConfig("h2mem1")

  logger.info("SCS started")

  private def init(db: Database): Unit = {
    logger.info("Database initialization started")
    val setup = DBIO.seq(
      dataTable.schema.create
    )

    Await.result(db.run(setup), 2.seconds)
    logger.info("Database initialization completed")
  }

  try {
    init(db)
  } finally
    db.close()

  logger.info("SCS stopped")*/
}
