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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeclarationView
import services.SessionService
import scala.concurrent.ExecutionContext
import models.{AreYouTheEntity, UserAnswers}
import pages._

class DeclarationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionService: SessionService,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val isAgent = isTheUserAgent(request.userAnswers)
    Ok(view(isAgent))
  }

  def confirm: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val updatedAnswers = request.userAnswers.copy(madeDeclaration = true)

    for {
      _ <- sessionService.set(updatedAnswers)
    } yield Redirect(controllers.routes.TaskListController.onPageLoad)
  }

  private[controllers] def isTheUserAgent(userAnswers: UserAnswers): Boolean =
    userAnswers.get(AreYouTheEntityPage) match {
      case Some(AreYouTheEntity.YesIAm) => false
      case _                            => true
    }
}
