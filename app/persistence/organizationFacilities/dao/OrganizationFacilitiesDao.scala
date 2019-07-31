package persistence.organizationFacilities.dao

import java.time.LocalDateTime

import persistence.facility.model.Facility
import persistence.geo.model.Location
import persistence.organizationFacilities.model.OrganizationFacilities
import persistence.organization.model.{Organization, OrganizationEdit}
import persistence.organization.dao.OrganizationDao
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class OrganizationFacilitiesDao @javax.inject.Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // --[ リソース定義 ] --------------------------------------------------------
  lazy val slick = TableQuery[OrganizationFacilitiesTable]
  lazy val slick_o = TableQuery[OrganizationTable]
  lazy val slick_f = TableQuery[FacilityTable]


  // --[ データ処理定義 ] ------------------------------------------------------

  /**
    * group by句を用いて、紐付くFacilitiesが存在するOrganizationの一覧と
    * 紐付くFacilityの数を返す
    */
  def findAllOrganizationFacilities =
    db.run{
      slick
        .groupBy(_.organizationId).map{
        case (s, results) => (s -> results.length)
      }.result
    }

  /**
    * Organization.Idを引数に取り、
    * それに紐付く全てのFacilityを返す
    */

  def getAllFacilities(id: Organization.Id) =
    db.run{
      slick_f
        .filter(_.id.in
          (slick
            .filter(_.organizationId === id)
            .map(p => p.facilityId)
          )
        )
        .result
    }

  /**
    * 紐づくFacilityの追加を行う
    */
  def insertOrganizationFacilities(organizationId: Organization.Id, facilityId: Facility.Id) = {
    val insertData : OrganizationFacilities = OrganizationFacilities(
      None,
      organizationId,
      facilityId,
    )
    db.run {
      slick returning slick.map(_.id) += insertData
    }
  }

  /**
    * 紐づくFacilityの削除を行う
    */
  def deleteOrganizationFacilities(organizationId: Organization.Id, facilityId: Facility.Id) = {
    db.run{
      slick
        .filter(row => (row.organizationId === organizationId) && (row.facilityId === facilityId))
        .delete
    }
  }

  class OrganizationFacilitiesTable(tag: Tag) extends Table[OrganizationFacilities](tag, "organization_facilities") {

    // Table's columns
    /* @1 */ def id = column[OrganizationFacilities.Id]    ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def organizationId= column[Organization.Id]    ("organization_id")
    /* @3 */ def facilityId          = column[Facility.Id]         ("facility_id")
    /* @4 */ def updatedAt     = column[LocalDateTime]  ("updated_at")
    /* @5 */ def createdAt     = column[LocalDateTime]  ("created_at")

    // The * projection of the table
    def * = (
      id.?, organizationId, facilityId,
      updatedAt, createdAt
    ) <> (
      /** The bidirectional mappings : Tuple(table) => Model */
      (OrganizationFacilities.apply _).tupled,
      /** The bidirectional mappings : Model => Tuple(table) */
      (v: TableElementType) => OrganizationFacilities.unapply(v).map(_.copy(
        _5 = LocalDateTime.now
      ))
    )
  }

  class OrganizationTable(tag: Tag) extends Table[Organization](tag, "organization") {


    // Table's columns
    /* @1 */ def id            = column[Facility.Id]    ("id", O.PrimaryKey, O.AutoInc)
    /* @2 */ def locationId    = column[Location.Id]    ("location_id")
    /* @3 */ def chineseName          = column[String]         ("chinese_name")
    /* @4 */ def phoneticName       = column[String]         ("phonetic_name")
    /* @5 */ def englishName   = column[String]         ("english_name")
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
