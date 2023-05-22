/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.notification

import java.util.UUID
import controllers.actions._
import javax.inject.Inject
import models.Mode
import navigation.NotificationNavigator
import pages.TrustAddressLookupPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import services.AddressLookupService
import play.api.mvc.{Call, Result}
import play.api.Logging
import models.requests.{DataRequest}
import models.Error

import scala.concurrent.{ExecutionContext, Future}

class TrustAddressLookupController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: NotificationNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        addressLookupService: AddressLookupService
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def lookupAddress(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val continueUrl = routes.TrustAddressLookupController.retrieveConfirmedAddress(mode)
    implicit val mApi = messagesApi

    addressLookupService.getTrustAddressLookupRedirect(continueUrl).fold(
      {e: Error =>
        logger.error(s"Error initialising Address Lookup: $e")
        Future.failed(e.throwable.getOrElse(new Exception(e.message)))
      },
      url => Future.successful(Redirect(Call("GET", url.toString)))
    ).flatten

  }

  def retrieveConfirmedAddress(mode: Mode,  id: Option[UUID] = None): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    id match {
      case None => Future.successful(Redirect(routes.TrustAddressLookupController.lookupAddress(mode)))
      case Some(addressId) => retrieveConfirmedAddressFromAddressLookupFrontend(mode, addressId)
    }
  }

  private def retrieveConfirmedAddressFromAddressLookupFrontend(mode: Mode,  addressId: UUID)(implicit request: DataRequest[AnyContent]): Future[Result] = 
    addressLookupService.retrieveUserAddress(addressId).fold(
      {e: Error =>
        logger.error(s"Error updating Address Lookup address: $e") 
        Future.failed(e.throwable.getOrElse(new Exception(e.message)))
      },
      address => for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustAddressLookupPage, address))
        _              <- sessionService.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(TrustAddressLookupPage, mode, updatedAnswers))
    ).flatten
}
