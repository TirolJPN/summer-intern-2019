package controllers.organization

import model.component.util.ViewValuePageLayout
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, MessagesControllerComponents, MessagesRequest}
import persistence.organization.dao.OrganizationDao
import persistence.organization.model.OrganizationEdit
import persistence.organization.model.Organization.formForOrganization
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
import model.site.organization.{SiteViewValueOrganization, SiteViewValueOrganizationList}
import model.component.util.ViewValuePageLayout

class OrganizationController @javax.inject.Inject()(
  val organizationDao: OrganizationDao,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

  /**
    * 組織一覧ページ
    */
  def list = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organizationSeq <- organizationDao.findAll
    } yield {
      val vv = SiteViewValueOrganizationList(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organizations = organizationSeq
      )
      Ok(views.html.site.organization.list.Main(vv))
    }
  }

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
        organization = organization,
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
          Redirect(routes.OrganizationController.list())
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
    Redirect(routes.OrganizationController.list())
  }

  /**
    * 既存組織情報の詳細
    */
  def detail(organizationId: Long) = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organization <- organizationDao.get(organizationId)
    } yield {
      val vv = SiteViewValueOrganization(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organization = organization
      )

      Ok(views.html.site.organization.detail.Main(vv))
    }
  }


  /**
    * 既存組織情報の編集
    */
  def edit(organizationId: Long) = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organization <- organizationDao.get(organizationId)
    } yield {
      val vv = SiteViewValueOrganization(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organization = organization
      )

      Ok(views.html.site.organization.edit.Main(
        vv, organizationId,
        formForOrganization.fill(
          OrganizationEdit(
            Option(organization.get.locationId),
            Option(organization.get.chineseName),
            Option(organization.get.phoneticName),
            Option(organization.get.englishName)
          )
        )
      ))
    }
  }

  def update(organizationId: Long) = Action {implicit request =>
    val formValues = formForOrganization.bindFromRequest.get
    organizationDao.update(organizationId, formValues)
    Redirect(routes.OrganizationController.list())
  }

  def delete(organizationId: Long) = Action { implicit request =>
    organizationDao.delete(organizationId)
    Redirect(routes.OrganizationController.list())
  }
}