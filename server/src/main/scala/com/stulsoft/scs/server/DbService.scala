/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import com.stulsoft.scs.server.data.Data
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

/**
  * Database service
  *
  * @author Yuriy Stul
  */
class DbService(val db: Database) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def get(key: String): Future[Option[Data]] = Future {
    try {
      Integer.valueOf(key)
//      Some(Data(key, "the value", 0L))
      Option(Data(key, "the value", 0L))
    } catch {
      case _ : Exception => None
    }
  }

  def put(key: String, json: String): Future[Option[Boolean]] = ???

  def delete(key: String): Future[Option[Boolean]] = ???
}
