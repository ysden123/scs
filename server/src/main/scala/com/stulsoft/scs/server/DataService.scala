/*
 * Copyright (c) 2017. Yuriy Stul
 */
package com.stulsoft.scs.server

import com.stulsoft.scs.common.data.Data
import com.stulsoft.scs.server.data.{DataTableDAO, Ttl, TtlTableDAO}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Data service
  *
  * @author Yuriy Stul
  */
class DataService() extends LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Returns a data for specified key
    *
    * @param key specifies the data to find
    * @return the data for specified key
    */
  def getData(key: String): Future[Option[Data]] = Future {
    try {
      Await.result(DataTableDAO.getData(db, key), 2.seconds)
    } catch {
      case _: Exception => None
    }
  }

  /**
    * Stores a data into database
    *
    * @param data the data to store
    * @return result of saving
    */
  def putData(data: Data): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.putData(db, data.key, data.value, data.ttl), 2.seconds)
      TtlTableDAO.putTtl(db, Ttl(data.key, data.ttl, System.currentTimeMillis))
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }

  /**
    * Deletes a data with specified key
    *
    * @param key specifies the data to delete
    * @return result of deleting
    */
  def deleteData(key: String): Future[Option[Boolean]] = Future {
    try {
      Await.result(DataTableDAO.deleteData(db, key), 2.seconds)
      TtlTableDAO.deleteTtl(db, key)
      Option(true)
    } catch {
      case _: Exception => Option(false)
    }
  }
}
