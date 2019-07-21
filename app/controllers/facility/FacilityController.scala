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
import views.html.site.facility.`new`.Main


// 施設
//~~~~~~~~~~~~~~~~~~~~~
class FacilityController @javax.inject.Inject()(
  val facilityDao: FacilityDAO,
  val daoLocation: LocationDAO,
  cc: MessagesControllerComponents
) extends AbstractController(cc) with I18nSupport {
  implicit lazy val executionContext = defaultExecutionContext

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

  def update(facilityId: Long) = Action { implicit request =>
    val formValues = formForFacility.bindFromRequest.get
    facilityDao.updateFacility(facilityId, formValues)
    Redirect(routes.FacilityController.list())
  }


  def new = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facility <- None
    } yield {
      val vv = SiteViewValueFacility(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facility = facility
      )
      Ok(views.html.site.facility.new.Main(formForFacility))
  }



  /**
    * 施設一覧ページ
    */
  def list = Action.async { implicit request =>
    for {
      locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
      facilitySeq <- facilityDao.findAll
    } yield {
      val vv = SiteViewValueFacilityList(
        layout     = ViewValuePageLayout(id = request.uri),
        location   = locSeq,
        facilities = facilitySeq
      )
      Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch))
    }
  }

  /**
   * 施設検索
   */
  def search = Action.async { implicit request =>
    formForFacilitySearch.bindFromRequest.fold(
      errors => {
       for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- facilityDao.findAll
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          BadRequest(views.html.site.facility.list.Main(vv, errors))
        }
      },
      form   => {
        for {
          locSeq      <- daoLocation.filterByIds(Location.Region.IS_PREF_ALL)
          facilitySeq <- form.locationIdOpt match {
            case Some(id) =>
              for {
                locations   <- daoLocation.filterByPrefId(id)
                facilitySeq <- facilityDao.filterByLocationIds(locations.map(_.id))
              } yield facilitySeq
            case None     => facilityDao.findAll
          }
        } yield {
          val vv = SiteViewValueFacilityList(
            layout     = ViewValuePageLayout(id = request.uri),
            location   = locSeq,
            facilities = facilitySeq
          )
          Ok(views.html.site.facility.list.Main(vv, formForFacilitySearch.fill(form)))
        }
      }
    )
  }
}
