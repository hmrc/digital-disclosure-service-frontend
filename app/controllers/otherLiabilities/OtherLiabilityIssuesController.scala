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

package controllers.otherLiabilities

import controllers.actions._
import forms.OtherLiabilityIssuesFormProvider
import javax.inject.Inject
import models._
import models.OtherLiabilityIssues._
import navigation.OtherLiabilitiesNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.otherLiabilities.OtherLiabilityIssuesView

import scala.concurrent.{ExecutionContext, Future}

import play.api.Logging

class OtherLiabilityIssuesController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OtherLiabilitiesNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: OtherLiabilityIssuesFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: OtherLiabilityIssuesView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(OtherLiabilityIssuesPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(OtherLiabilityIssuesPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            _              <- sessionService.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(OtherLiabilityIssuesPage, mode, clearedAnswers, hasValueChanged))
        }
      )
  }

  def changedPages(existingUserAnswers: UserAnswers, value: Set[OtherLiabilityIssues]): (List[QuestionPage[_]], Boolean) = {
    existingUserAnswers.get(OtherLiabilityIssuesPage) match {
      case Some(preferences) if preferences != value =>
        val pages = preferences.flatMap {
            case InheritanceTaxIssues if !value.contains(InheritanceTaxIssues) => Some(DescribeTheGiftPage)
            case Other if !value.contains(Other) => Some(WhatOtherLiabilityIssuesPage)
            case _ => None
          }.toList
        (pages, true)
      case _ => (Nil, false)
    }

  }

}