/*
 * This file is part of Nextbeat services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package controllers.facility

import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, MessagesControllerComponents, MessagesRequest}
import persistence.facility.dao.FacilityDAO
import persistence.facility.model.Facility.formForFacilitySearch
import persistence.facility.model.Facility.formForFacility
import persistence.facility.model.{Facility, FacilityEdit}
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
import model.site.facility.{SiteViewValueFacility, SiteViewValueFacilityList}
import model.component.util.ViewValuePageLayout


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class FacilityController @javax.inject.Inject()(
  val facilityDao: FacilityDAO,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents,
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext
  val pageMaxLength: Int = 10;         // 1ページあたりの最大表示数
  /**
    * 施設編集ページ
    */

  def edit(facilityId: Long) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facility <- facilityDao.get(facilityId)
    } yield {
      val vv = SiteViewValueFacility(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facility = facility
      )

      Ok(views.html.site.facility.edit.Main(vv, facilityId ,
        formForFacility.fill(
          FacilityEdit(
            Option(facility.get.locationId),
            Option(facility.get.name),
            Option(facility.get.address),
            Option(facility.get.description)
          )
        )
      ))
    }
  }

  // 新規Facilityの追加


  // Facilityの更新
  def update(facilityId: Long) = Action { implicit request =>
    val formValues = formForFacility.bindFromRequest.get
    facilityDao.update(facilityId, formValues)
    Redirect(routes.FacilityController.list(0))
  }


  def add = Action.async { implicit request =>
    for {
      locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facility <- scala.concurrent.Future(None)
    } yield {
      val vv = SiteViewValueFacility(
        layout = ViewValuePageLayout(id = request.uri),
        location = locSeq,
        facility = facility,
      )
      Ok(views.html.site.facility.add.Main(vv, formForFacility))
    }
  }

  def insert = Action { implicit request =>
    formForFacility.bindFromRequest.fold(
      errors => {
        for {
          locSeq <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
        } yield {
//          BadRequest(views.html.site.app.new_user.Main(vv, errors))
          Redirect(routes.FacilityController.list(0))
        }
      },
      facility   => {
        for {
          _ <- facilityDao.insert(facility)
        } yield {
          // TODO: セッション追加処理
          Redirect("/recruit/intership-for-summer-21")
        }
      }
    )
    // facilityDao.insert(formValues)
    Redirect(routes.FacilityController.list(0))
  }

  def delete(facilityId: Long) = Action { implicit request =>
    facilityDao.delete(facilityId)
    Redirect(routes.FacilityController.list(0))
  }



  /**
    * 施設一覧ページ
    */
  def list(currentPage: Int) = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facilitySeq <- facilityDao.findAll(currentPage-1, pageMaxLength)
      facilityCount <- facilityDao.getCountFindAll
    } yield {
      val vv = SiteViewValueFacilityList(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilities = facilitySeq,
        facilityCount = facilityCount
      )
      Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch, currentPage, pageMaxLength))
    }
  }

  /**
   * 施設検索
   */
  def search(currentPage: Int) = Action.async { implicit request =>
    formForFacilitySearch.bindFromRequest.fold(
      errors => {
       for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll(currentPage-1, pageMaxLength)
          facilityCount <- facilityDao.getCountFindAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq,
            facilityCount = facilityCount
          )
          BadRequest(views.html.site.facility.list.Main(vv, errors, currentPage, pageMaxLength))
        }
      },
      form   => {
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilityCount <- facilityDao.getCountFindAll
          facilitySeq <- form.locationIdOpt match {
            case Some(id) =>
              for {
                locations   <- daoLocation.filterByPrefId(id)
                facilitySeq <- facilityDao.filterByLocationIds(locations.map(_.id)),
              } yield facilitySeq
            case None     => facilityDao.findAll(currentPage, pageMaxLength)
          }
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq,
            facilityCount = facilityCount
          )
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch.fill(form), currentPage, pageMaxLength))
        }
      }
    )
  }
}
