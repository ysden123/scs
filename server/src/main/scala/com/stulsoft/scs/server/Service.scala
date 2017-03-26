/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import com.stulsoft.scs.server.data.Data
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val dataFormat: RootJsonFormat[Data] = jsonFormat3(Data)
}

/**
  * @author Yuriy Stul
  */
class Service extends Directives with JsonSupport with LazyLogging {
  private lazy val dbService = new DbService()
  val route: Route = pathPrefix("v.0.0") {
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
          entity(as[Data]) {
            data => {
              Await.result(dbService.put(data), 2.seconds) match {
                case Some(value) =>
                  complete(value.toString)
                case _ =>
                  complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Failure</h1>"))
              }
            }
          }
        } ~
        delete {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Delete for key $key</h1>"))
        }
    }
  }
}
