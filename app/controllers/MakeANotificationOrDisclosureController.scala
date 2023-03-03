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

package controllers

import controllers.actions._
import forms.MakeANotificationOrDisclosureFormProvider
import javax.inject.Inject
import models.{UserAnswers, SubmissionType}
import models.MakeANotificationOrDisclosure._
import navigation.Navigator
import pages.MakeANotificationOrDisclosurePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.MakeANotificationOrDisclosureView
import java.time.Instant

import scala.concurrent.{ExecutionContext, Future}

class MakeANotificationOrDisclosureController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionService: SessionService,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       formProvider: MakeANotificationOrDisclosureFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: MakeANotificationOrDisclosureView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData) {
    implicit request =>
      Ok(view(form))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          val newSubmissionType = if (value == MakeANotification) SubmissionType.Notification else SubmissionType.Disclosure
          val updatedAnswers = request.userAnswers match {
            case Some(ua) => ua.copy(submissionType = newSubmissionType)
            case None => UserAnswers(request.userId, UserAnswers.defaultSubmissionId, newSubmissionType, created = Instant.now)
          }

          for {
            _ <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(MakeANotificationOrDisclosurePage, updatedAnswers))
        }
      )
  }
}
