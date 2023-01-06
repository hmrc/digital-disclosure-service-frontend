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
import forms.WhichYearDoesThisOffshoreDisclosureRelateToFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, WhyAreYouMakingThisDisclosure}
import navigation.NotificationNavigator
import pages.{WhichYearDoesThisOffshoreDisclosureRelateToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhichYearDoesThisOffshoreDisclosureRelateToView

import scala.concurrent.{ExecutionContext, Future}

class WhichYearDoesThisOffshoreDisclosureRelateToController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: NotificationNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhichYearDoesThisOffshoreDisclosureRelateToFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhichYearDoesThisOffshoreDisclosureRelateToView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichYearDoesThisOffshoreDisclosureRelateToPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, numberOfYears(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, numberOfYears(request.userAnswers)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichYearDoesThisOffshoreDisclosureRelateToPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichYearDoesThisOffshoreDisclosureRelateToPage, mode, updatedAnswers))
      )
  }

  def numberOfYears(ua: UserAnswers): Int = {

    val didNotNotifyHasExcuse = WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse
    val deliberatelyDidNotNotify = WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify
    val deliberateInaccurateReturn = WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn
    val deliberatelyDidNotFile = WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile
    val inaccurateReturnNoCare = WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare
    val notFileHasExcuse = WhyAreYouMakingThisDisclosure.NotFileHasExcuse
    val didNotNotifyNoExcuse = WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse
    val inaccurateReturnWithCare = WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
  
    ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) => 
        if (
          value.contains(didNotNotifyHasExcuse) || 
          value.contains(deliberatelyDidNotNotify) || 
          value.contains(deliberateInaccurateReturn) || 
          value.contains(deliberatelyDidNotFile)) {19}
        else if (value.contains(inaccurateReturnNoCare)) {7}
        else if (
          value.contains(notFileHasExcuse) || 
          value.contains(didNotNotifyNoExcuse) || 
          value.contains(inaccurateReturnWithCare)) {5}
        else {0}
      case None => 0          
    }
  }
}
