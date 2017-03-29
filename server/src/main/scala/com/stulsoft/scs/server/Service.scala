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
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat3(Response)
}

/**
  * @author Yuriy Stul
  */
class Service(version: String) extends Directives with JsonSupport with LazyLogging {
  private lazy val dbService = new DbService()
  val route: Route = pathPrefix(version) {
    pathPrefix("key" / Remaining) { key =>
      get {
        Await.result(dbService.get(key), 2.seconds) match {
          case Some(value) =>
            complete(Response(200, Some(value), None))
          case _ =>
            complete(Response(204, None, Some("not found")))
        }
      } ~
        put {
          entity(as[Data]) {
            data => {
              Await.result(dbService.put(data), 2.seconds) match {
                case Some(value) =>
                  complete(Response(200, None, None))
                case _ =>
                  complete(Response(500, None, Some("internal error")))
              }
            }
          }
        } ~
        delete {
          Await.result(dbService.delete(key), 2.seconds) match {
            case Some(value) =>
              complete(Response(200, None, None))
            case _ =>
              complete(Response(500, None, Some("internal error")))
          }
        }
    }
  }
}
