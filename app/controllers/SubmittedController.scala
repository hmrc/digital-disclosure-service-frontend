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

import config.FrontendAppConfig
import controllers.actions._

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SubmittedView
import pages.{CorporationTaxLiabilityPage, DirectorLoanAccountLiabilitiesPage, OnshoreTaxYearLiabilitiesPage, TaxYearLiabilitiesPage, WhatIsTheCaseReferencePage}
import models.UserAnswers

class SubmittedController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredActionEvenSubmitted,
  val controllerComponents: MessagesControllerComponents,
  view: SubmittedView
)(implicit appConfig: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(reference: String): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val isNilDisclosure     = isAllLiabilitiesEmpty(request.userAnswers)
      val caseReferenceExists = request.userAnswers.get(WhatIsTheCaseReferencePage).isDefined
      Ok(view(caseReferenceExists, isNilDisclosure, reference))
  }

  def isAllLiabilitiesEmpty(ua: UserAnswers): Boolean = {
    val offshoreTaxYearEmpty = ua.get(TaxYearLiabilitiesPage).forall(_.isEmpty)
    val onshoreTaxYearEmpty  = ua.get(OnshoreTaxYearLiabilitiesPage).forall(_.isEmpty)
    val ctEmpty              = ua.get(CorporationTaxLiabilityPage).getOrElse(Set()).size == 0
    val dlEmpty              = ua.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Set()).size == 0

    offshoreTaxYearEmpty && onshoreTaxYearEmpty && ctEmpty && dlEmpty
  }
}
