package model.site.organization

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.organization.model.Organization

// 単体の組織
class SiteViewValueOrganization
(
  layout:      ViewValuePageLayout,
  location:    Seq[Location],
  organzation: Option[Organization]
)