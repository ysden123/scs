/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.http.scaladsl.server.{Directives, Route}
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

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
