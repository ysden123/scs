/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.common.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * @author Yuriy Stul
  */
trait JsonSupport  extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val dataFormat: RootJsonFormat[Data] = jsonFormat3(Data)
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat3(Response)
}
