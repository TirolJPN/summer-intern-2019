/*
 * This file is part of the MARIAGE services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package model.site.facility

import model.component.util.ViewValuePageLayout
import persistence.geo.model.Location
import persistence.facility.model.Facility

// 表示: 単体の施設
//~~~~~~~~~~~~~~~~~~~~~
case class SiteViewValueFacility
(
  layout:   ViewValuePageLayout,
  location: Seq[Location],
  facility: Option[Facility]
)
