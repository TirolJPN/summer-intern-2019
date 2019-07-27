/*
 * This file is part of the Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package persistence.facility.dao

import java.time.LocalDateTime

import persistence.facility.model

import scala.concurrent.Future
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import persistence.facility.model.Facility
import persistence.facility.model.FacilityEdit
import persistence.geo.model.Location

// DAO: 施設情報
//~~~~~~~~~~~~~~~~~~
class FacilityDAO @javax.inject.Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // --[ リソース定義 ] --------------------------------------------------------
  lazy val slick = TableQuery[FacilityTable]

  // --[ データ処理定義 ] ------------------------------------------------------
  /**
   * 施設を取得
   */
  def get(id: Facility.Id): Future[Option[Facility]] =
    db.run {
      slick
        .filter(_.id === id)
        .result.headOption
    }

  /**
   * 施設を全件取得する
   */
  def findAll: Future[Seq[Facility]] =
    db.run {
      slick.result
    }

  /**
   * 地域から施設を取得
   * 検索業件: ロケーションID
   */
  def filterByLocationIds(locationIds: Seq[Location.Id]): Future[Seq[Facility]] =
    db.run {
      slick
        .filter(_.locationId inSet locationIds)
        .result
    }

  def update (facilityId:Long, formValues: FacilityEdit) =
    db.run {
      slick
        .filter(_.id === facilityId)
        .map(p => (p.locationId, p.name, p.address, p.description))
        .update((formValues.locationId.get, formValues.name.get, formValues.address.get, formValues.description.get))
    }

  def insert(Recvdata: FacilityEdit): Future[Facility.Id] = {
    val insertData: Facility = Facility(None, Recvdata.locationId.get, Recvdata.name.get,  Recvdata.address.get,  Recvdata.description.get)
    db.run {
      //      data.id match {
      //        case None    => slick returning slick.map(_.id) += data
      //        case Some(_) => DBIO.failed(
      //          new IllegalArgumentException("The given object is already assigned id.")
      //        )
      //      }
      slick returning slick.map(_.id) += insertData
    }
  }

  def delete (facilityId:Long) =
    db.run {
      slick
        .filter(_.id === facilityId)
        .delete
    }


  // --[ テーブル定義 ] --------------------------------------------------------
  class FacilityTable(tag: Tag) extends Table[Facility](tag, "facility") {


    // Table's columns
    /* @1 */ def id            = column[Facility.Id]    ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def locationId    = column[Location.Id]    ("location_id")
    /* @3 */ def name          = column[String]         ("name")
    /* @4 */ def address       = column[String]         ("address")
    /* @5 */ def description   = column[String]         ("description")
    /* @6 */ def updatedAt     = column[LocalDateTime]  ("updated_at")
    /* @7 */ def createdAt     = column[LocalDateTime]  ("created_at")

    // The * projection of the table
    def * = (
      id.?, locationId, name, address, description,
      updatedAt, createdAt
    ) <> (
      /** The bidirectional mappings : Tuple(table) => Model */
      (Facility.apply _).tupled,
      /** The bidirectional mappings : Model => Tuple(table) */
      (v: TableElementType) => Facility.unapply(v).map(_.copy(
        _6 = LocalDateTime.now
      ))
    )
  }
}
