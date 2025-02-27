package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.organization.model.Organization

// 単体の組織
case class SiteViewValueOrganizationList
(
  layout:      ViewValuePageLayout,
  location:    Seq[Location],
  organizations: Seq[Organization],
  organizationCount: Int,
  organizationFacilities: Seq[(Organization.Id, Int)]
)