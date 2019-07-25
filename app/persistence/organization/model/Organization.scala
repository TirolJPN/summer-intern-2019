package persistence.organization.model

import play.api.data._
import play.api.data.Forms._
import java.time.LocalDateTime

import persistence.geo.model.Location
import persistence.facility.model.Facility

/*
  Organizationは最低限下記の情報を持ちます。
      名前(漢字、ふりがな、英語名)を持つ
      住所を持つ。住所は番地・建物名まで含み、なおかつLocationと紐づく
      複数のFacilityと紐づく
 */
case class Organization {
  id:               Option[Organization.Id],    // 組織ID
  locationId:       Location.Id,                // 地域ID
  chineseName:      String,                     // 名前(漢字)
  phoneticName:     String,                     // 名前(ふりがな)
  englishName:      String,                     // 名前(英語名)

}

case class OrganizationFacilities {
  id:              Organization.id,             // 紐付けるOrganization id
  facilityid:      Facility.id,                 // 紐づけるFacility id
}


// コンパニオンオブジェクト
object Organization {

  //  --[ 管理ID ]---------------------------------------------------------------
  type Id = Long
}