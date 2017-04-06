package com.stulsoft.scs.server.data

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import scala.concurrent.Future

/**
  * Holds a TTL details
  *
  * @param key       an data object key
  * @param ttl       the TTL
  * @param startDate the start date
  * @author Yuriy Stul
  */
case class Ttl(key: String, ttl: Long, startDate: Long)

class TtlTable(tag: Tag) extends Table[Ttl](tag, "TTLS") {
  def * : ProvenShape[Ttl] = (key, ttl, startDate) <> (Ttl.tupled, Ttl.unapply)

  def key: Rep[String] = column[String]("KEY", O.PrimaryKey)

  def ttl: Rep[Long] = column[Long]("TTL")

  def startDate: Rep[Long] = column[Long]("START_DATE")
}

object TtlTableDAO extends TableQuery(new TtlTable(_)) with LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getTtl(db: Database, key: String): Future[Option[Ttl]] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    db.run(this.filter(_.key === key).result).map(_.headOption)
  }

  def putTtl(db: Database, ttl: Ttl): Future[Unit] = {
    require(db != null, "db undefined")
    require(ttl != null, "ttl undefined or empty")
    Future {
      db.run(this.insertOrUpdate(ttl))
    }
  }

  def deleteTtl(db: Database, key: String): Future[Unit] = {
    require(db != null, "db undefined")
    require(key != null && !key.isEmpty, "key undefined or empty")
    Future {
      db.run(this.filter(_.key === key).delete)
    }
  }
}
