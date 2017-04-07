/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.http.scaladsl.server.{Directives, Route}
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response, _}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * SCS Service for server
  *
  * @author Yuriy Stul
  */
class Service extends Directives with JsonSupport with LazyLogging {
  private lazy val dbService = new DataService()
  val route: Route = pathPrefix(version) {
    pathPrefix("key" / Remaining) { key =>
      get {
        Await.result(dbService.getData(key), 2.seconds) match {
          case Some(value) =>
            complete(Response(200, Some(value), None))
          case _ =>
            complete(Response(204, None, Some("not found")))
        }
      } ~
        put {
          entity(as[Data]) {
            data => {
              Await.result(dbService.putData(data), 2.seconds) match {
                case Some(_) =>
                  complete(Response(200, None, None))
                case _ =>
                  complete(Response(500, None, Some("internal error")))
              }
            }
          }
        } ~
        delete {
          Await.result(dbService.deleteData(key), 2.seconds) match {
            case Some(value) =>
              complete(Response(200, None, None))
            case _ =>
              complete(Response(500, None, Some("internal error")))
          }
        }
    }
  }
}