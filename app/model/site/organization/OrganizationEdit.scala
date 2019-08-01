package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.facility.model.Facility
import persistence.geo.model.Location
import persistence.organization.model.Organization

// 単体の組織
case class SiteViewValueOrganizationEdit
(
  layout:      ViewValuePageLayout,
  location:    Seq[Location],
  organization: Option[Organization],
  relatedFacilities: Seq[Facility],
  unrelatedFacilities: Seq[Facility],
)