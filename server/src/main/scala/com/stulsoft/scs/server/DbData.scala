/*
 * Copyright (c) 2017. Yuriy Stul
 */

package com.stulsoft.scs.server

import com.stulsoft.scs.common.data.Data
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import scala.concurrent.Future


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
  def getData(db: Database, key: String): Future[Option[Data]] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    db.run(this.filter(_.key === key).result).map(_.headOption)
  }

  /**
    * Stores (inserts or updates) a data
    *
    * @param db    the database
    * @param key   the key
    * @param value the value
    * @param ttl   the ttl
    * @return Future
    */
  def putData(db: Database, key: String, value: String, ttl: Long): Future[Unit] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    require(value != null && !value.isEmpty, "value undefined or empty")
    Future {
      db.run(this.insertOrUpdate(Data(key, value, ttl)))
    }
  }

  def deleteData(db: Database, key: String): Future[Unit] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    Future {
      db.run(this.filter(_.key === key).delete)
    }
  }
}