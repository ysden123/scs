/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response, _}
import com.stulsoft.scs.server.actor.{DbGet, DbPut}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * SCS Service for server
  *
  * @author Yuriy Stul
  */
class Service(val dbActor: ActorRef) extends Directives with JsonSupport with LazyLogging {
  require(dbActor != null, "dbActor should be defined")
  private lazy val dbService = new DataService()
  implicit private val timeout = Timeout(2.seconds)
  val route: Route = pathPrefix(version) {
    pathPrefix("key" / Remaining) { key =>
      get {
        Await.result(dbActor ? DbGet(key), timeout.duration) match {
          case Some(x: Data) => complete(Response(200, Some(x), None))
          case _ => complete(Response(204, None, Some("not found")))
        }
      } ~
        put {
          entity(as[Data]) {
            data => {
              Await.result(dbActor ? DbPut(data), timeout.duration) match {
                case _: Option[_] => complete(Response(200, None, None))
                case _ =>
                  complete(Response(500, None, Some("internal error")))
              }
            }
          }
        } ~
        delete {
          Await.result(dbService.deleteData(key), timeout.duration) match {
            case _: Option[_] => complete(Response(200, None, None))
            case _ =>
              complete(Response(500, None, Some("internal error")))
          }
        }
    }
  }
}