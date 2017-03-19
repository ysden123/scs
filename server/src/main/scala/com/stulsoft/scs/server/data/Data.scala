/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server.data

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Holds a data object details
  *
  * @param key   the key
  * @param value the value
  * @param ttl   the TTL (maximum life time)
  */
case class Data(key: String, value: String, ttl: Long)

/**
  * Table definition for Data objects
  *
  * @param tag the tag
  */
class DataTable(tag: Tag) extends Table[Data](tag, "DATAS") {
  def * : ProvenShape[Data] = (key, value, ttl) <> (Data.tupled, Data.unapply)

  def key: Rep[String] = column[String]("KEY", O.PrimaryKey)

  def value: Rep[String] = column[String]("VALUE")

  def ttl: Rep[Long] = column[Long]("TTL")
}

/**
  * DataTable DAO
  */
object DataTableDAO extends TableQuery(new DataTable(_)) with LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Returns a data for specified key
    *
    * @param db  the database
    * @param key the key
    * @return the data for specified key
    */
//  def getData(db: Database, key: String): Future[Option[Data]] = {
  def getData(db: Database, key: String): Seq[Data] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    logger.debug("getting data...")
//    val data = Await.result(db.run(this.filter(_.key === key).result), 2.seconds).map(_.headOption)
    val data = Await.result(db.run(this.filter(_.key === key).result), 2.seconds)
    logger.debug("got data!")
    data
  }

  def putData(db: Database, key: String, value: String, ttl: Long): Unit = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    require(value != null && !value.isEmpty, "value undefined or empty")
    logger.debug("putting data...")
    val r =Await.result(db.run(this.insertOrUpdate(Data(key, value, ttl))), 2.seconds)
    println(s"r=$r")
    logger.debug("put data!")
  }
}