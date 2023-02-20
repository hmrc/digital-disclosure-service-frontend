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

package controllers.onshore

import controllers.actions._
import forms.WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, RelatesTo}
import navigation.OnshoreNavigator
import pages.{WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, RelatesToPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhatOnshoreLiabilitiesDoYouNeedToDiscloseView

import scala.concurrent.{ExecutionContext, Future}

class WhatOnshoreLiabilitiesDoYouNeedToDiscloseController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OnshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhatOnshoreLiabilitiesDoYouNeedToDiscloseView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val isUserCompany = isTheUserACompany(request.userAnswers)

      Ok(view(preparedForm, mode, isUserCompany))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val isUserCompany = isTheUserACompany(request.userAnswers)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, isUserCompany))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, mode, updatedAnswers))
      )
  }

  def isTheUserACompany(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.ACompany) => true
      case _ => false
    }
  }
}
