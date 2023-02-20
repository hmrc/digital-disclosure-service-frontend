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
import forms.WhichOnshoreYearsFormProvider
import javax.inject.Inject
import models.{UserAnswers, Behaviour, Mode}
import navigation.OnshoreNavigator
import pages.WhichOnshoreYearsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{OnshoreWhichYearsService, SessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhichOnshoreYearsView
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import pages.WhyAreYouMakingThisOnshoreDisclosurePage

import scala.concurrent.{ExecutionContext, Future}

class WhichOnshoreYearsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OnshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhichOnshoreYearsFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhichOnshoreYearsView,
                                        onshoreWhichYearsService: OnshoreWhichYearsService
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichOnshoreYearsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, populateChecklist(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, populateChecklist(request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichOnshoreYearsPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichOnshoreYearsPage, mode, updatedAnswers))
      )
  }

  def populateChecklist(ua: UserAnswers)(implicit messages: Messages): Seq[CheckboxItem] = {

    import models.WhyAreYouMakingThisOnshoreDisclosure._
 
    val behaviour = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
      case Some(value) if (value.contains(DidNotNotifyNoExcuse) || 
          value.contains(DeliberatelyDidNotNotify) || 
          value.contains(DeliberateInaccurateReturn) || 
          value.contains(DeliberatelyDidNotFile)) =>  Behaviour.Deliberate
      case Some(value) if (value.contains(InaccurateReturnNoCare)) => Behaviour.Careless      
      case _ => Behaviour.ReasonableExcuse 
    }

    onshoreWhichYearsService.checkboxItems(behaviour)
  }

}
