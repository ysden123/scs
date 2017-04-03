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
import akka.stream.scaladsl.{Sink, Source}
import com.stulsoft.scs.common.data.{Data, JsonSupport, Response, _}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * SCS client service
  *
  * @author Yuriy Stul
  */
protected class Service(val host: String, val port: Int) extends JsonSupport with LazyLogging {
  require(host != null && !host.isEmpty, "host should be defined")
  implicit val system = ActorSystem("scs-client-system")
  implicit val materializer = ActorMaterializer()

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Returns a data for specified key
    *
    * @param key specifies the data to find
    * @return the data for specified key
    */
  def getData(key: String): Future[Response] = {
    val source = Source.single(HttpRequest(method = HttpMethods.GET, uri = Uri(path = Path(s"/$version/key/$key"))))
    val flow = Http().outgoingConnection(host, port).mapAsync(1) {
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }

  /**
    * Stores a data
    *
    * @param data the data to store
    * @return result of saving the data
    */
  def putData(data: Data): Future[Response] = {
    val entity = Await.result(Marshal(data).to[RequestEntity], 1.seconds)
    val source = Source.single(HttpRequest(method = HttpMethods.PUT
      , uri = Uri(path = Path(s"/$version/key/${data.key}"))
      , entity = entity))
    val flow = Http().outgoingConnection(host, port).mapAsync(1) {
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }

  /**
    * Deletes a data with specified key
    *
    * @param key specifies the data to delete
    * @return result of deleting the data
    */
  def deleteData(key: String): Future[Response] = {
    val source = Source.single(HttpRequest(method = HttpMethods.DELETE, uri = Uri(path = Path(s"/$version/key/$key"))))
    val flow = Http().outgoingConnection(host, port).mapAsync(1) {
      r => Unmarshal(r.entity).to[Response]
    }
    source.via(flow).runWith(Sink.head)
  }
}

protected object Service {
  def apply(host: String, port: Int): Service = new Service(host, port)
}
