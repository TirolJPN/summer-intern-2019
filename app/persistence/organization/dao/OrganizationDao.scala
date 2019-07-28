package persistence.organization.dao

import java.time.LocalDateTime

import persistence.facility.model.Facility
import persistence.geo.model.Location

import scala.concurrent.Future
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import persistence.organization.model.{Organization, OrganizationEdit}
import persistence.organization.model.OrganizationEdit

class OrganizationDao @javax.inject.Inject() (
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // --[ リソース定義 ] --------------------------------------------------------
  lazy val slick = TableQuery[OrganizationTable]

  // --[ データ処理定義 ] ------------------------------------------------------
  /**
    * organizationを1件取得
    */
  def get(id: Organization.Id): Future[Option[Organization]] =
    db.run {
      slick
        .filter(_.id === id)
        .result.headOption
    }

  /**
   * organizationを全件取得
   */
  def findAll: Future[Seq[Organization]] =
    db.run {
      slick.result
    }

  /**
    * idをパラメータにして、organizationを一件編集
    */

   def update (organizationId:Long, formValues: OrganizationEdit) =
     db.run {
       slick
         .filter(_.id === organizationId)
         .map( p => (p.locationId, p.chineseName, p.phoneticName, p.englishName))
         .update((
           formValues.locationId.get,
           formValues.chineseName.get,
           formValues.phoneticName.get,
           formValues.englishName.get,
         ))
     }

  def insert (RecvData: OrganizationEdit): Future[Organization.Id] = {
    val insertData : Organization = Organization(
        None,
        RecvData.locationId.get,
        RecvData.chineseName.get,
        RecvData.phoneticName.get,
        RecvData.englishName.get,
    )
    db.run {
      slick returning slick.map(_.id) += insertData
    }
  }

  def delete (organizationId: Long) =
    db.run {
      slick
        .filter(_.id === organizationId)
        .delete
    }

  class OrganizationTable(tag: Tag) extends Table[Organization](tag, "organization") {


    // Table's columns
    /* @1 */ def id            = column[Facility.Id]    ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def locationId    = column[Location.Id]    ("location_id")
    /* @3 */ def chineseName          = column[String]         ("chineseName")
    /* @4 */ def phoneticName       = column[String]         ("phoneticName")
    /* @5 */ def englishName   = column[String]         ("englishName")
    /* @6 */ def updatedAt     = column[LocalDateTime]  ("updated_at")
    /* @7 */ def createdAt     = column[LocalDateTime]  ("created_at")

    // The * projection of the table
    def * = (
      id.?, locationId, chineseName, phoneticName, englishName,
      updatedAt, createdAt
    ) <> (
      /** The bidirectional mappings : Tuple(table) => Model */
      (Organization.apply _).tupled,
      /** The bidirectional mappings : Model => Tuple(table) */
      (v: TableElementType) => Organization.unapply(v).map(_.copy(
        _6 = LocalDateTime.now
      ))
    )
  }
}
