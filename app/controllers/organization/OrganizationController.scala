package controllers.organization

import model.component.util.ViewValuePageLayout
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, MessagesControllerComponents, MessagesRequest}
import persistence.organization.dao.OrganizationDao
import persistence.organization.model.OrganizationEdit
import persistence.organization.model.Organization.formForOrganization
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
import model.site.organization.SiteViewValueOrganization
import model.component.util.ViewValuePageLayout

class OrganizationController @javax.inject.Inject()(
  val organizationDao: OrganizationDao,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 組織追加ページ
    */
  def add = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organization <- scala.concurrent.Future(None)
    } yield {
      val vv = SiteViewValueOrganization (
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organzation = organization,
      )
      Ok(views.html.site.organization.add.Main(vv, formForOrganization))
    }
  }

  def insert = Action { implicit request =>
    formForOrganization.bindFromRequest.fold(
      errors => {
        for {
          locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
        } yield {
          //          BadRequest(views.html.site.app.new_user.Main(vv, errors))
          Redirect("/recruit/intership-for-summer-21")
        }
      },
      organization   => {
        for {
          _ <- organizationDao.insert(organization)
        } yield {
          // TODO: セッション追加処理
          Redirect("/recruit/intership-for-summer-21")
        }
      }
    )
    Redirect("/recruit/intership-for-summer-21")
  }
}