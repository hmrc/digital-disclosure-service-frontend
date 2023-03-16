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

import controllers.actions._
import forms.HowWouldYouPreferToBeContactedFormProvider
import models.HowWouldYouPreferToBeContacted.{Email, Telephone}

import javax.inject.Inject
import models.{HowWouldYouPreferToBeContacted, Mode, UserAnswers}
import navigation.NotificationNavigator
import pages.{HowWouldYouPreferToBeContactedPage, QuestionPage, YourEmailAddressPage, YourPhoneNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.HowWouldYouPreferToBeContactedView

import scala.concurrent.{ExecutionContext, Future}

class HowWouldYouPreferToBeContactedController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: NotificationNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: HowWouldYouPreferToBeContactedFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: HowWouldYouPreferToBeContactedView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(HowWouldYouPreferToBeContactedPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HowWouldYouPreferToBeContactedPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            _              <- sessionService.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(HowWouldYouPreferToBeContactedPage, mode, clearedAnswers, hasValueChanged))
        }
      )
  }

  def changedPages(existingUserAnswers: UserAnswers, value: Set[HowWouldYouPreferToBeContacted]): (List[QuestionPage[_]], Boolean) = {
    existingUserAnswers.get(HowWouldYouPreferToBeContactedPage) match {
      case Some(preferences) if preferences != value =>
        val pages = preferences.flatMap {
            case Email if !value.contains(Email) => Some(YourEmailAddressPage)
            case Telephone if !value.contains(Telephone) => Some(YourPhoneNumberPage)
            case _ => None
          }.toList
        (pages, true)
      case _ => (Nil, false)
    }
  }

  def isDisclosure(userAnswers: UserAnswers): Boolean = {
    userAnswers.submissionType match {
      case Disclosure => true
      case _ => false
    }
  }
}
