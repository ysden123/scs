/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/**
  * @author Yuriy Stul
  */
class Client extends JsonSupport with LazyLogging {
  implicit val system = ActorSystem("scs-client-system")
  implicit val materializer = ActorMaterializer()

  private val version = "v.0.0"

  def getData(key: String): Future[Response] = {
    val source = Source.single(HttpRequest(uri = Uri(path = Path(s"/$version/key/1"))))
    val flow = Http().outgoingConnection("localhost", 8080).mapAsync(1){
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }

  def putData(data: Data): Future[Option[Boolean]] = ???

  def deleteData(key: String): Future[Option[Boolean]] = ???

  logger.info("Started client")

  logger.info("Stopped client")
}
