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
import forms.OfferLetterFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OfferLetterView
import viewmodels.offshore.CheckYourAnswersViewModelCreation
import models.UserAnswers
import models.address.Address
import play.api.i18n.Messages
import models.RelatesTo._

import scala.concurrent.{ExecutionContext, Future}

class OfferLetterController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: OfferLetterFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: OfferLetterView,
                                        viewModelCreation: CheckYourAnswersViewModelCreation
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(OfferLetterPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val name = getEntityName(request.userAnswers)
      val addressLines = getAddressLines(request.userAnswers)
      val totalAmount = viewModelCreation.create(request.userAnswers).liabilitiesTotal.toInt
      val entityKey = getEntityKey(request.userAnswers)
      Ok(view(preparedForm, name, addressLines, totalAmount, entityKey, getAgentName(request.userAnswers)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          val name = getEntityName(request.userAnswers)
          val addressLines = getAddressLines(request.userAnswers)
          val totalAmount = viewModelCreation.create(request.userAnswers).liabilitiesTotal.toInt
          val entityKey = getEntityKey(request.userAnswers)
          Future.successful(BadRequest(view(formWithErrors, name, addressLines, totalAmount, entityKey, getAgentName(request.userAnswers))))}
        ,
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(OfferLetterPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(OfferLetterPage, updatedAnswers))
      )
  }

  def getEntityName(userAnswers: UserAnswers): String = {
    val entityQuestion = userAnswers.get(RelatesToPage) 
    val areYouTheIndividualQuestion = userAnswers.get(AreYouTheIndividualPage)

    val namePage: QuestionPage[String] = (entityQuestion, areYouTheIndividualQuestion) match {
      case (Some(AnIndividual), Some(false)) => WhatIsTheIndividualsFullNamePage
      case (Some(ACompany), _) => WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage
      case (Some(ALimitedLiabilityPartnership), _) => WhatIsTheLLPNamePage
      case (Some(ATrust), _) => WhatIsTheTrustNamePage
      case (Some(AnEstate), _) => WhatWasTheNameOfThePersonWhoDiedPage
      case _ => WhatIsYourFullNamePage
    }

    userAnswers.get(namePage).getOrElse("")
  }

  def getAgentName(userAnswers: UserAnswers): String = {
    userAnswers.get(WhatIsYourFullNamePage).getOrElse("")
  }

  def getAddressLines(userAnswers: UserAnswers)(implicit messages: Messages): String = {
    val entityQuestion = userAnswers.get(RelatesToPage) 
    val areYouTheIndividualQuestion = userAnswers.get(AreYouTheIndividualPage)

    val addressPage: QuestionPage[Address] = (entityQuestion, areYouTheIndividualQuestion) match {
      case (Some(AnIndividual), Some(false)) => IndividualAddressLookupPage
      case (Some(ACompany), _) => CompanyAddressLookupPage
      case (Some(ALimitedLiabilityPartnership), _) => LLPAddressLookupPage
      case (Some(ATrust), _) => TrustAddressLookupPage
      case (Some(AnEstate), _) => EstateAddressLookupPage
      case _ => YourAddressLookupPage
    }
    userAnswers.get(addressPage).map(_.getAddressLines.mkString("<br>")).getOrElse("")
  }

  def getEntityKey(userAnswers: UserAnswers): String = userAnswers.get(RelatesToPage).getOrElse(AnIndividual).toString

}
