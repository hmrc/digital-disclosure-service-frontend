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
import services.{DisclosureSubmissionService, SessionService, UAToDisclosureService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OfferLetterView
import models.UserAnswers
import models.address.Address
import play.api.i18n.Messages
import models.RelatesTo._
import viewmodels.TotalAmounts

import scala.concurrent.{ExecutionContext, Future}

class OfferLetterController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: OfferLetterFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: OfferLetterView,
  uaToDisclosureService: UAToDisclosureService,
  submissionService: DisclosureSubmissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(OfferLetterPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    val name                 = getEntityName(request.userAnswers)
    val addressLines         = getAddressLines(request.userAnswers)
    val disclosure           = uaToDisclosureService.uaToFullDisclosure(request.userAnswers)
    val totalAmount          = TotalAmounts(disclosure).amountDueTotal.toInt
    val entityKey            = getEntityKey(request.userAnswers)
    val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
    Ok(
      view(
        preparedForm,
        name,
        addressLines,
        totalAmount,
        entityKey,
        getAgentName(request.userAnswers),
        areTheyTheIndividual
      )
    )
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val name                 = getEntityName(request.userAnswers)
          val addressLines         = getAddressLines(request.userAnswers)
          val disclosure           = uaToDisclosureService.uaToFullDisclosure(request.userAnswers)
          val totalAmount          = TotalAmounts(disclosure).amountDueTotal.toInt
          val entityKey            = getEntityKey(request.userAnswers)
          val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
          Future.successful(
            BadRequest(
              view(
                formWithErrors,
                name,
                addressLines,
                totalAmount,
                entityKey,
                getAgentName(request.userAnswers),
                areTheyTheIndividual
              )
            )
          )
        },
        value =>
          request.userAnswers.metadata.reference match {
            case Some(ref) => Future.successful(Redirect(navigator.submitPage(ref)))
            case None      =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(OfferLetterPage, value))
                _              <- sessionService.set(updatedAnswers)
                reference      <- submissionService.submitDisclosure(updatedAnswers)
                userReference   = updatedAnswers.get(WhatIsTheCaseReferencePage).getOrElse(reference)
              } yield Redirect(navigator.submitPage(userReference))
          }
      )
  }

  def getEntityName(userAnswers: UserAnswers): String = {
    val namePage: QuestionPage[String] = userAnswers.get(RelatesToPage) match {
      case Some(AnIndividual) if !userAnswers.isTheUserTheIndividual => WhatIsTheIndividualsFullNamePage
      case Some(ACompany)                                            => WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage
      case Some(ALimitedLiabilityPartnership)                        => WhatIsTheLLPNamePage
      case Some(ATrust)                                              => WhatIsTheTrustNamePage
      case Some(AnEstate)                                            => WhatWasTheNameOfThePersonWhoDiedPage
      case _                                                         => WhatIsYourFullNamePage
    }

    userAnswers.get(namePage).getOrElse("")
  }

  def getAgentName(userAnswers: UserAnswers): String =
    userAnswers.get(WhatIsYourFullNamePage).getOrElse("")

  def getAddressLines(userAnswers: UserAnswers)(implicit messages: Messages): String = {
    val entityQuestion = userAnswers.get(RelatesToPage)

    val addressPage: QuestionPage[Address] = entityQuestion match {
      case Some(AnIndividual) if !userAnswers.isTheUserTheIndividual => IndividualAddressLookupPage
      case Some(ACompany)                                            => CompanyAddressLookupPage
      case Some(ALimitedLiabilityPartnership)                        => LLPAddressLookupPage
      case Some(ATrust)                                              => TrustAddressLookupPage
      case Some(AnEstate)                                            => EstateAddressLookupPage
      case _                                                         => YourAddressLookupPage
    }
    userAnswers.get(addressPage).map(_.getAddressLines.mkString("<br>")).getOrElse("")
  }

  def getEntityKey(userAnswers: UserAnswers): String = userAnswers.get(RelatesToPage).getOrElse(AnIndividual).toString

}
