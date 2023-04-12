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

package services

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._
import uk.gov.hmrc.time.{CurrentTaxYear, TaxYear}
import com.google.inject.{Inject, ImplementedBy, Singleton}
import models.Behaviour

@Singleton
class OffshoreWhichYearsServiceImpl @Inject() (timeService: TimeService) extends OffshoreWhichYearsService with CurrentTaxYear {

  def now = () => timeService.date

  val DELIBERATE_YEARS = 19
  val REASONABLE_EXCUSE_LEGISLATION_START = 2015
  val CARELESS_LEGISLATION_START = 2013
  val YEARS_TO_GO_BACK = 13

  def checkboxItems(behaviour: Behaviour)(implicit messages: Messages): Seq[CheckboxItem] = { 

    val numberOfYears = getNumberOfYearsForBehaviour(behaviour)

    val checkboxItem = behaviour match {
      case Behaviour.ReasonableExcuse => Seq(createReasonableExcusePriorToCheckbox(numberOfYears, current)(messages))
      case Behaviour.Careless         => Seq(createCarelessPriorToCheckbox(numberOfYears, current)(messages))
      case Behaviour.Deliberate       => Seq(createDeliberatePriorToCheckbox(numberOfYears, current)(messages))
    }

    createYearCheckboxes(numberOfYears, current)(messages) ++ checkboxItem
  }

  def getNumberOfYearsForBehaviour(behaviour: Behaviour): Int = behaviour match {
    case Behaviour.ReasonableExcuse => getNumberOfYears(TaxYear(REASONABLE_EXCUSE_LEGISLATION_START))
    case Behaviour.Careless         => getNumberOfYears(TaxYear(CARELESS_LEGISLATION_START))
    case Behaviour.Deliberate       => DELIBERATE_YEARS
  }

  def getNumberOfYears(legislationStartYear: TaxYear): Int = {
    val earliestDate = List(current.back(YEARS_TO_GO_BACK).startYear, legislationStartYear.startYear).max
    current.startYear - earliestDate - 1
  }

  def getEarliestYearByBehaviour(behaviour: Behaviour): Int = {
    val yearsToGoBack = getNumberOfYearsForBehaviour(behaviour)
    current.back(yearsToGoBack+1).startYear
  }

  def createYearCheckboxes(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): Seq[CheckboxItem] = {
    Range.inclusive(0, numberOfYears-1).toList.map {i =>
      val taxYear = currentTaxYear.back(i+2)
      CheckboxItemViewModel(
        content = Text(messages(s"whichYears.checkbox", s"${taxYear.startYear}", s"${taxYear.finishYear}")),
        fieldId = "value",
        index   = i,
        value   = taxYear.startYear.toString
      )
    }
  }

  def createReasonableExcusePriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "reasonableExcusePriorTo"
    )
  }

  def createCarelessPriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "carelessPriorTo"
    )
  }

  def createDeliberatePriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index = numberOfYears,
      value = "deliberatePriorTo"
    )
  }
    
}

@ImplementedBy(classOf[OffshoreWhichYearsServiceImpl])
trait OffshoreWhichYearsService {
  def checkboxItems(behaviour: Behaviour)(implicit messages: Messages): Seq[CheckboxItem]
  def getEarliestYearByBehaviour(behaviour: Behaviour): Int
}