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
import persistence.facility.model.Facility.formForFacilityEdit
import persistence.facility.model.{Facility, FacilityEdit}
import persistence.geo.model.Location
import persistence.geo.dao.LocationDAO
import model.site.facility.{SiteViewValueFacility, SiteViewValueFacilityList}
import model.component.util.ViewValuePageLayout
import play.api.data._
import play.api.data.Forms._


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

  // Web上のフォームの各の属性値をController側で決めてる？
//  val todoForm: Form[String] = Form(
//    "name" -> nonEmptyText
//  )
  /*
   *     /* @2 */ def locationId    = column[Location.Id]    ("location_id")
         /* @3 */ def name          = column[String]         ("name")
         /* @4 */ def address       = column[String]         ("address")
         /* @5 */ def description   = column[String]         ("description")
   */

  //  新しいTodoを追加するフォームがあるページを作成するAction?
  //  def todoNew = Action { implicit  request: MessagesRequest[AnyContent] =>
  //    Ok(views.html.createForm(todoForm))
  //  }

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
        formForFacilityEdit.fill(
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
