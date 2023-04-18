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
import models._
import navigation.OnshoreNavigator
import pages._
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{OnshoreWhichYearsService, SessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhichOnshoreYearsView
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

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
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichOnshoreYearsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val isCTCompleted = request.userAnswers.get(CorporationTaxLiabilityPage).getOrElse(Seq()).nonEmpty
      val isDLCompleted = request.userAnswers.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Seq()).nonEmpty

      Ok(view(preparedForm, mode, populateChecklist(request.userAnswers), isCTCompleted, isDLCompleted))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val isCTCompleted = request.userAnswers.get(CorporationTaxLiabilityPage).getOrElse(Seq()).nonEmpty
      val isDLCompleted = request.userAnswers.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Seq()).nonEmpty

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, populateChecklist(request.userAnswers), isCTCompleted, isDLCompleted))),

        value => {
          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichOnshoreYearsPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            filteredAnswers <- Future.fromTry(filterDeselectedYears(value, clearedAnswers))
            _ <- sessionService.set(filteredAnswers)
          } yield {
            Redirect(navigator.nextPage(WhichOnshoreYearsPage, mode, filteredAnswers, hasValueChanged))
          }
        }
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

  def changedPages(userAnswers: UserAnswers, newValue: Set[OnshoreYears]): (List[QuestionPage[_]], Boolean) = {
    val hasChanged = !userAnswers.get(WhichOnshoreYearsPage).contains(newValue) || !areYearsMissing(userAnswers, newValue)
    val missingYearPageList = if (hasChanged) List(NotIncludedSingleTaxYearPage, NotIncludedMultipleTaxYearsPage) else Nil

    val priorToList = newValue.intersect(Set[OnshoreYears](PriorToFiveYears, PriorToThreeYears, PriorToNineteenYears)).size match {
      case 0 => List(TaxBeforeThreeYearsOnshorePage, TaxBeforeFiveYearsOnshorePage, TaxBeforeNineteenYearsOnshorePage)
      case 1 => List(LettingPropertyPage)
      case _ => Nil
    }

    val pagesToClear = missingYearPageList ::: priorToList
    (pagesToClear, hasChanged)
  }

  def filterDeselectedYears(newValue: Set[OnshoreYears], ua: UserAnswers): Try[UserAnswers] = {
    val offshoreTaxYears = newValue.collect { case OnshoreYearStarting(y) => OnshoreYearStarting(y) }.toSeq
    updateLiabilitiesPage(offshoreTaxYears, ua)
  }

  def updateLiabilitiesPage(newTaxYears: Seq[OnshoreYearStarting], ua: UserAnswers): Try[UserAnswers] =
    ua.get(OnshoreTaxYearLiabilitiesPage)
      .fold[Try[UserAnswers]](Success(ua))(liabilities => updateTaxYearLiabilities(newTaxYears, ua, liabilities))

  def updateTaxYearLiabilities(newTaxYears: Seq[OnshoreYearStarting], ua: UserAnswers, liabilities: Map[String, OnshoreTaxYearWithLiabilities]): Try[UserAnswers] = {
    val taxYearsAsStrings = newTaxYears.map(_.toString)
    val newLiabilities = liabilities.filter { case (year, _) => taxYearsAsStrings.contains(year) }
    ua.set(OnshoreTaxYearLiabilitiesPage, newLiabilities)
  }

  def areYearsMissing(userAnswers: UserAnswers, newValue: Set[OnshoreYears]) = {
    val onshoreTaxYears = newValue.collect { case OnshoreYearStarting(y) => OnshoreYearStarting(y) }.toSeq
    val liabilitiesMap = userAnswers.get(OnshoreTaxYearLiabilitiesPage).getOrElse(Map.empty)
    onshoreTaxYears.forall(year => liabilitiesMap.contains(year.toString))
  }

}
