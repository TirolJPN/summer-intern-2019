package persistence.organization.model


import java.time.LocalDateTime
import persistence.facility.model.Facility



case class OrganizationFacilities (
  id:              Option[OrganizationFacilities.Id],
  organizationId:  Organization.Id, // 紐付けるOrganization id
  facilityId:      Facility.Id, // 紐づけるFacility id
  updatedAt:   LocalDateTime = LocalDateTime.now,  // データ更新日
  createdAt:   LocalDateTime = LocalDateTime.now   // データ作成日
)


// コンパニオンオブジェクト
object OrganizationFacilities {

  //  --[ 管理ID ]---------------------------------------------------------------
  type Id = Long

}

