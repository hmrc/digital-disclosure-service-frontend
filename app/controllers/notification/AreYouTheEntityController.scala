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
import forms.AreYouTheEntityFormProvider
import javax.inject.Inject
import models.{AreYouTheEntity, Mode, RelatesTo, UserAnswers}
import navigation.NotificationNavigator
import pages.{AreYouTheEntityPage, QuestionPage, RelatesToPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.AreYouTheEntityView
import pages.notification.SectionPages

import scala.concurrent.{ExecutionContext, Future}

class AreYouTheEntityController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AreYouTheEntityFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AreYouTheEntityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with SectionPages {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
    val form   = formProvider(entity)

    val preparedForm = request.userAnswers.get(AreYouTheEntityPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, entity, request.userAnswers.isDisclosure))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val form   = formProvider(entity)

      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, entity, request.userAnswers.isDisclosure))),
          value => {
            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AreYouTheEntityPage, value))
              clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedAnswers)
            } yield Redirect(navigator.nextPage(AreYouTheEntityPage, mode, clearedAnswers, hasValueChanged))
          }
        )
  }

  def changedPages(userAnswers: UserAnswers, newAnswer: AreYouTheEntity): (List[QuestionPage[_]], Boolean) = {

    import RelatesTo._
    import AreYouTheEntity._

    val oldAnswer = userAnswers.get(AreYouTheEntityPage)

    val answerHasChanged = Some(newAnswer) != oldAnswer
    val isIndividual     = userAnswers.get(RelatesToPage).getOrElse(AnIndividual) == AnIndividual

    val pagesToClear = (oldAnswer, newAnswer) match {
      case _ if !answerHasChanged               => Nil
      case (_, YesIAm) if isIndividual          => aboutYouPages ::: aboutIndividualPages ::: areYouTheOrganisationPages
      case (Some(YesIAm), _) if isIndividual    => aboutYouPages
      case (Some(IAmAnAccountantOrTaxAgent), _) => areYouTheOrganisationPages
      case _                                    => Nil
    }
    (pagesToClear, answerHasChanged)

  }
}
