package controllers.organization

import model.component.util.ViewValuePageLayout
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, MessagesControllerComponents, MessagesRequest}
import persistence.organization.dao.OrganizationDao
import persistence.organization.model.OrganizationEdit
import persistence.organization.model.Organization.formForOrganization
import persistence.geo.model.Location
import persistence.facility.model.Facility
import persistence.geo.dao.LocationDAO
import model.site.organization.{SiteViewValueOrganization, SiteViewValueOrganizationDetail, SiteViewValueOrganizationEdit, SiteViewValueOrganizationList}
import model.component.util.ViewValuePageLayout
import persistence.organizationFacilities.dao.OrganizationFacilitiesDao

class OrganizationController @javax.inject.Inject()(
  val organizationDao: OrganizationDao,
  val daoLocation: LocationDAO,
  val organizationFacilitiesDao: OrganizationFacilitiesDao,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext
  val pageMaxLength: Int = 10;         // 1ページあたりの最大表示数

  /**
    * 組織一覧ページ
    */
  def list(currentPage: Int) = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organizationSeq <- organizationDao.findAll(currentPage-1, pageMaxLength)
      organizationFacilitiesSeq <- organizationFacilitiesDao.findAll
      organizationCount <- organizationDao.getCountFindAll
    } yield {
      val vv = SiteViewValueOrganizationList(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organizations = organizationSeq,
        organizationCount = organizationCount,
        /**
          *  organizationFacilitiesの型ははSeq[organizationFacilities]ではなく
          *  Seq[(Organization.Id, Int)]であることに注意
          *        →findAllOrganizationFacilitiesがcountを含むqueryのため
          */
        organizationFacilities = organizationFacilitiesSeq
      )
      Ok(views.html.site.organization.list.Main(vv, currentPage, pageMaxLength))
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
        organization = organization
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
          Redirect(routes.OrganizationController.list(1))
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
    Redirect(routes.OrganizationController.list(1))
  }

  /**
    * 既存組織情報の詳細
    */
  def detail(organizationId: Long) = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      organization <- organizationDao.get(organizationId)
      facilities <- organizationFacilitiesDao.getRelatedAll(organizationId)
    } yield {
      val vv = SiteViewValueOrganizationDetail(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organization = organization,
        facilities = facilities
      )
      println(facilities)
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
      relatedFacilities <- organizationFacilitiesDao.getRelatedAll(organizationId)
      unrelatedFacilities <- organizationFacilitiesDao.getUnrelatedAll(organizationId)
    } yield {
      val vv = SiteViewValueOrganizationEdit(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        organization = organization,
        relatedFacilities = relatedFacilities,
        unrelatedFacilities = unrelatedFacilities,
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
    Redirect(routes.OrganizationController.list(1))
  }

  def delete(organizationId: Long) = Action { implicit request =>
    organizationDao.delete(organizationId)
    Redirect(routes.OrganizationController.list(1))
  }

  /**
    * organization - facilities の更新のためのアクション
    */

  def insertOrganizationFacilities(organizationId: Long, facilityId: Long) = Action { implicit request =>
    organizationFacilitiesDao.insert(organizationId, facilityId)
    Redirect(routes.OrganizationController.edit(organizationId))
  }

  def deleteOrganizationFacilities(organizationId: Long, facilityId: Long) = Action { implicit request =>
    organizationFacilitiesDao.delete(organizationId, facilityId)
    Redirect(routes.OrganizationController.edit(organizationId))
  }
}