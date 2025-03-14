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
import models._
import pages._
import navigation.OffshoreNavigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{OffshoreWhichYearsService, SessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhichYearsView
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import play.api.i18n.Messages
import scala.util.{Success, Try}

import scala.concurrent.{ExecutionContext, Future}

class WhichYearsController @Inject() (
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
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhichYearsPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, populateChecklist(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, populateChecklist(request.userAnswers)))),
          value => {

            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)

            for {
              updatedAnswers  <- Future.fromTry(request.userAnswers.set(WhichYearsPage, value))
              clearedAnswers  <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              filteredAnswers <- Future.fromTry(filterDeselectedYears(value, clearedAnswers))
              _               <- sessionService.set(filteredAnswers)
            } yield Redirect(navigator.nextPage(WhichYearsPage, mode, filteredAnswers, hasValueChanged))
          }
        )
  }

  def populateChecklist(ua: UserAnswers)(implicit messages: Messages): Seq[CheckboxItem] = {

    import models.WhyAreYouMakingThisDisclosure._

    val behaviour = ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value)
          if value.contains(DidNotNotifyNoExcuse) ||
            value.contains(DeliberatelyDidNotNotify) ||
            value.contains(DeliberateInaccurateReturn) ||
            value.contains(DeliberatelyDidNotFile) =>
        Behaviour.Deliberate
      case Some(value) if value.contains(InaccurateReturnNoCare) => Behaviour.Careless
      case _                                                     => Behaviour.ReasonableExcuse
    }

    offshoreWhichYearsService.checkboxItems(behaviour)
  }

  def changedPages(userAnswers: UserAnswers, newValue: Set[OffshoreYears]): (List[QuestionPage[_]], Boolean) = {
    val missingYearsCount = userAnswers.inverselySortedOffshoreTaxYears
      .map(ty => TaxYearStarting.findMissingYears(ty.toList).size)
      .getOrElse(0)

    val missingYearPageList         =
      if (missingYearsCount == 0) List(YouHaveNotIncludedTheTaxYearPage, YouHaveNotSelectedCertainTaxYearPage) else Nil
    val reasonableExcusePriorToList =
      if (!newValue.contains(ReasonableExcusePriorTo)) List(TaxBeforeFiveYearsPage) else Nil
    val carelessPriorToList         = if (!newValue.contains(CarelessPriorTo)) List(TaxBeforeSevenYearsPage) else Nil
    val deliberatePriorToList       = if (!newValue.contains(DeliberatePriorTo)) List(TaxBeforeNineteenYearsPage) else Nil

    val pagesToClear =
      missingYearPageList ::: reasonableExcusePriorToList ::: carelessPriorToList ::: deliberatePriorToList
    val hasChanged   = (Some(newValue) != userAnswers.get(WhichYearsPage)) || !areYearsMissing(userAnswers, newValue)

    (pagesToClear, hasChanged)
  }

  def filterDeselectedYears(newValue: Set[OffshoreYears], ua: UserAnswers): Try[UserAnswers] = {

    val offshoreTaxYears = newValue.collect { case TaxYearStarting(y) => TaxYearStarting(y) }.toSeq

    for {
      uaWithLiabilities <- updateLiabilitiesPage(offshoreTaxYears, ua)
      updatedUa         <- updateForeignTaxCreditsPage(offshoreTaxYears, uaWithLiabilities)
    } yield updatedUa
  }

  def updateLiabilitiesPage(newTaxYears: Seq[TaxYearStarting], ua: UserAnswers): Try[UserAnswers] =
    ua.get(TaxYearLiabilitiesPage)
      .fold[Try[UserAnswers]](Success(ua))(liabilities => updateTaxYearLiabilities(newTaxYears, ua, liabilities))

  def updateForeignTaxCreditsPage(newTaxYears: Seq[TaxYearStarting], ua: UserAnswers): Try[UserAnswers] =
    ua.get(ForeignTaxCreditPage)
      .fold[Try[UserAnswers]](Success(ua))(taxCredits => updateForeignTaxCredits(newTaxYears, ua, taxCredits))

  def updateTaxYearLiabilities(
    newTaxYears: Seq[TaxYearStarting],
    ua: UserAnswers,
    liabilities: Map[String, TaxYearWithLiabilities]
  ): Try[UserAnswers] = {
    val taxYearsAsStrings = newTaxYears.map(_.toString)
    val newLiabilities    = liabilities.filter { case (year, withLiabilities) => taxYearsAsStrings.contains(year) }
    ua.set(TaxYearLiabilitiesPage, newLiabilities)
  }

  def updateForeignTaxCredits(
    newTaxYears: Seq[TaxYearStarting],
    ua: UserAnswers,
    taxCredits: Map[String, BigInt]
  ): Try[UserAnswers] = {
    val taxYearsAsStrings    = newTaxYears.map(_.toString)
    val newForeignTaxCredits = taxCredits.filter { case (year, deduction) => taxYearsAsStrings.contains(year) }
    ua.set(ForeignTaxCreditPage, newForeignTaxCredits)
  }

  def areYearsMissing(userAnswers: UserAnswers, newValue: Set[OffshoreYears]) = {
    val offshoreTaxYears = newValue.collect { case TaxYearStarting(y) => TaxYearStarting(y) }.toSeq
    val liabilitiesMap   = userAnswers.get(TaxYearLiabilitiesPage).getOrElse(Map.empty)
    offshoreTaxYears.forall(year => liabilitiesMap.contains(year.toString))
  }

}
