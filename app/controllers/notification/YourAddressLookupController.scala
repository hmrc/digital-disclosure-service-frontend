/*
 * Copyright 2022 HM Revenue & Customs
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
import pages.YourAddressLookupPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import services.AddressLookupService
import play.api.mvc.{Call, Result}
import play.api.Logging
import models.requests.{DataRequest}
import models.Error
import pages._
import models._
import scala.concurrent.{ExecutionContext, Future}

class YourAddressLookupController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: NotificationNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        addressLookupService: AddressLookupService
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def lookupAddress(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val continueUrl = routes.YourAddressLookupController.retrieveConfirmedAddress(mode) 

    addressLookupService.getYourAddressLookupRedirect(continueUrl, request.userAnswers).fold(
      {e: Error =>
        logger.error(s"Error initialising Address Lookup: $e")
        Future.failed(e.throwable.getOrElse(new Exception(e.message)))
      },
      url => Future.successful(Redirect(Call("GET", url.toString)))
    ).flatten

  }

  def retrieveConfirmedAddress(mode: Mode,  id: Option[UUID] = None): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    id match {
      case None => Future.successful(Redirect(routes.YourAddressLookupController.lookupAddress(mode)))
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
        updatedAnswers <- Future.fromTry(request.userAnswers.set(YourAddressLookupPage, address))
        _              <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(YourAddressLookupPage, mode, updatedAnswers))
    ).flatten
}
