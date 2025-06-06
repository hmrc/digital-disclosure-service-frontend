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
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView
import services.SessionService
import models.UserAnswers
import scala.concurrent.{ExecutionContext, Future}
import navigation.Navigator

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  view: IndexView,
  sessionService: SessionService,
  navigator: Navigator
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    if (request.isAgent) {
      Future.successful(Ok(view(controllers.routes.CaseManagementController.onPageLoad(1).url, request.isAgent)))
    } else {
      for {
        uaOpt <-
          sessionService.getIndividualUserAnswers(request.userId, request.sessionId, UserAnswers.defaultSubmissionId)
        url    = navigator.indexNextPage(uaOpt).url
        _     <- uaOpt.map(sessionService.set).getOrElse(Future.successful(true))
      } yield Ok(view(url, request.isAgent))
    }

  }

}
