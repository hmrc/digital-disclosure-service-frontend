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

package controllers.offshore

import controllers.actions._
import forms.WhichYearsFormProvider
import javax.inject.Inject
import models.{Behaviour, Mode, UserAnswers}
import navigation.OffshoreNavigator
import pages.{WhichYearsPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{OffshoreWhichYearsService, SessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhichYearsView
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import play.api.i18n.Messages

import scala.concurrent.{ExecutionContext, Future}

class WhichYearsController @Inject()(
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: OffshoreNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhichYearsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhichYearsView,
  offshoreWhichYearsService: OffshoreWhichYearsService
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichYearsPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichYearsPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichYearsPage, mode, updatedAnswers))
      )
  }

  def populateChecklist(ua: UserAnswers)(implicit messages: Messages): Seq[CheckboxItem] = {

    import models.WhyAreYouMakingThisDisclosure._
 
    val behaviour = ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) if (value.contains(DidNotNotifyNoExcuse) || 
          value.contains(DeliberatelyDidNotNotify) || 
          value.contains(DeliberateInaccurateReturn) || 
          value.contains(DeliberatelyDidNotFile)) =>  Behaviour.Deliberate
      case Some(value) if (value.contains(InaccurateReturnNoCare)) => Behaviour.Careless      
      case _ => Behaviour.ReasonableExcuse 
    }

    offshoreWhichYearsService.checkboxItems(behaviour)
  }

}
