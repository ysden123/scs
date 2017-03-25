/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directive, Directives}
import com.stulsoft.scs.server.Server.logger
import com.stulsoft.scs.server.data.Data
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val dataFormat = jsonFormat3(Data)
}

/**
  * @author Yuriy Stul
  */
class Service extends Directives with JsonSupport with LazyLogging {
  private val db = Database.forConfig("h2mem1")
  private val dbService = new DbService(db)
  private val version = "v.0.0"
  init(db)

  val route = pathPrefix(version) {
    pathPrefix("key" / Remaining) { key =>
      get {
        Await.result(dbService.get(key), 2.seconds) match {
          case Some(value) =>
            complete(value)
          case _ =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Failure</h1>"))
        }
      } ~
        put {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Put for key $key</h1>"))
        } ~
        delete {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Delete for key $key</h1>"))
        }
    }
  }

  private def init(db: Database): Unit = {
    logger.info("Database initialization started")
    val setup = DBIO.seq(
      dataTable.schema.create
    )

    Await.result(db.run(setup), 2.seconds)
    logger.info("Database initialization completed")
  }
}
