/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * @author Yuriy Stul
  */
class Client extends JsonSupport with LazyLogging {
  implicit val system = ActorSystem("scs-client-system")
  implicit val materializer = ActorMaterializer()
  import scala.concurrent.ExecutionContext.Implicits.global

  private val version = "v.0.0"

  def getData(key: String): Future[Response] = {
    val source = Source.single(HttpRequest(method = HttpMethods.GET, uri = Uri(path = Path(s"/$version/key/1"))))
    val flow = Http().outgoingConnection("localhost", 8080).mapAsync(1) {
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }

  def putData(data: Data): Future[Response] = {
    val entity = Await.result(Marshal(data).to[RequestEntity], 1.seconds)
    val source = Source.single(HttpRequest(method = HttpMethods.PUT
      , uri = Uri(path = Path(s"/$version/key/1"))
      , entity = entity))
    val flow = Http().outgoingConnection("localhost", 8080).mapAsync(1) {
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }

  def deleteData(key: String): Future[Option[Boolean]] = ???

  logger.info("Started client")

  logger.info("Stopped client")
}
