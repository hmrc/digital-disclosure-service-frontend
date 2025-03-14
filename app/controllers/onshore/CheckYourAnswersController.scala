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
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.CheckYourAnswersView
import viewmodels.onshore.CheckYourAnswersViewModelCreation
import pages.{OffshoreLiabilitiesPage, OnshoreLiabilitiesPage, OnshoreTaxYearLiabilitiesPage}
import models.UserAnswers

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModelCreation: CheckYourAnswersViewModelCreation
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val viewModel     = viewModelCreation.create(request.userAnswers)
    val taxYearExists = request.userAnswers.get(OnshoreTaxYearLiabilitiesPage).forall(_.isEmpty)
    Ok(view(viewModel, !taxYearExists, isOnshoreOffshoreLiabilitiesPresent(request.userAnswers)))
  }

  def isOnshoreOffshoreLiabilitiesPresent(userAnswers: UserAnswers): Boolean =
    (userAnswers.get(OffshoreLiabilitiesPage), userAnswers.get(OnshoreLiabilitiesPage)) match {
      case (Some(true), Some(true)) => true
      case _                        => false
    }
}
