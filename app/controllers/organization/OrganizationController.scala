package controllers.organization

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, MessagesControllerComponents, MessagesRequest}
import persistence.organization.dao.OrganizationDao
import persistence.organization.model.OrganizationEdit
import persistence.organization.model.Organization.formOrganizationEdit
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO




class OrganizationController @javax.inject.Inject()(
  val organizationDao: OrganizationDao,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 組織追加ページ
    */
  def add = Action.async { implicit request=>
    for {
      loqSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organization <- scala.concurrent.Future(None)
    } yield {
        // pass each params to view
    }
  }
}