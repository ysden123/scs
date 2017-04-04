package com.stulsoft.scs.client

import com.stulsoft.scs.common.data.{Data, Response}

import scala.concurrent.Future

/**
  * SCS client API
  *
  * @author Yuriy Stul
  */
sealed class Client(val host: String, val port: Int) {
  require(host != null && !host.isEmpty, "host should be defined")
  private val OPEN_CLIENT = " \"Open client before\""
  private val service = Service(host, port)

  /**
    * Initializes a client
    */
  def open(): Unit = {
    service.start()
  }

  /**
    * Closes a client
    */
  def close(): Unit = {
    service.stop()
  }

  /**
    * Returns a data for specified key
    *
    * @param key specifies the data to find
    * @return the data for specified key
    */
  def getData(key: String): Future[Response] = {
    if (!service.isStarted) throw new RuntimeException(OPEN_CLIENT)
    service.getData(key)
  }

  /**
    * Stores a data
    *
    * @param data the data to store
    * @return result of saving the data
    */
  def putData(data: Data): Future[Response] = {
    if (!service.isStarted) throw new RuntimeException(OPEN_CLIENT)
    service.putData(data)
  }

  /**
    * Deletes a data with specified key
    *
    * @param key specifies the data to delete
    * @return result of deleting the data
    */
  def deleteData(key: String): Future[Response] = {
    if (!service.isStarted) throw new RuntimeException(OPEN_CLIENT)
    service.deleteData(key)
  }

}

object Client {
  def apply(host: String, port: Int): Client = new Client(host, port)
}