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
import forms.WhatIsTheNameOfTheOrganisationYouRepresentFormProvider
import javax.inject.Inject
import models.Mode
import navigation.NotificationNavigator
import pages.WhatIsTheNameOfTheOrganisationYouRepresentPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.WhatIsTheNameOfTheOrganisationYouRepresentView

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheNameOfTheOrganisationYouRepresentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhatIsTheNameOfTheOrganisationYouRepresentFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhatIsTheNameOfTheOrganisationYouRepresentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, request.userAnswers.isDisclosure))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, request.userAnswers.isDisclosure))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(WhatIsTheNameOfTheOrganisationYouRepresentPage, value))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(WhatIsTheNameOfTheOrganisationYouRepresentPage, mode, updatedAnswers))
        )
  }

}
