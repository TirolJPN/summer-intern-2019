package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.organization.model.Organization
import persistence.organization.model.OrganizationFacilities

// 単体の組織
case class SiteViewValueOrganizationList
(
  layout:      ViewValuePageLayout,
  location:    Seq[Location],
  organizations: Seq[Organization],
  organizationFacilities: Seq[(Organization.Id, Int)]
)