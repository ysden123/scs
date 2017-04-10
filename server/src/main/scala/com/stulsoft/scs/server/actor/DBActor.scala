package com.stulsoft.scs.server.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.stulsoft.scs.common.data.Data
import com.stulsoft.scs.server.data.{DataTableDAO, Ttl, TtlTableDAO}
import com.stulsoft.scs.server.db

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

case class DbGet(key: String)

case class DbPut(data: Data)

case class DbDelete(key: String)

/**
  * @author Yuriy Stul
  */
class DBActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case x: DbGet =>
      sender ! Await.result(getData(x.key), 2.seconds)
    case x: DbPut =>
      sender ! Await.result(putData(x.data), 2.seconds)
    case x: DbDelete =>
      sender ! Await.result(deleteData(x.key), 2.seconds)
  }

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

object TestForDBActor extends App {
  println("==>TestForDBActor")
  val system = ActorSystem("system")
  val dbActor = system.actorOf(Props[DBActor])
  Thread.sleep(500)
  implicit val timeout = Timeout(2.seconds)
  // add
  val futureAdd = dbActor ? DbPut(Data("123","text", 1000))
  val resultAdd = Await.result(futureAdd, timeout.duration)
  println(s"add: result is $resultAdd")

  // get
  val futureGet = dbActor ? DbGet("123")
  val resultGet = Await.result(futureGet, timeout.duration)
  println(s"get: result is $resultGet")
  system.terminate()
  println("<==TestForDBActor")
}