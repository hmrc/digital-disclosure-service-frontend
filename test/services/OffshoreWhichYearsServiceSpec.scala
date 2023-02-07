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

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import java.time.{LocalDateTime, LocalDate}
import models.Behaviour
import uk.gov.hmrc.time.TaxYear

class OffshoreWhichYearsServiceSpec extends SpecBase {

  def now = () => LocalDate.now()
  val taxYear2022 = TaxYear(2022)
  val taxYear2023 = TaxYear(2023)
  val taxYear2028 = TaxYear(2028)

  case class FakeTimeService(year: Int) extends TimeService {
    def now: LocalDateTime = LocalDateTime.of(year, 4, 6, 0, 0, 0, 0)
    def date: LocalDate = LocalDate.of(year, 4, 6)
  }

  val application = applicationBuilder(userAnswers = None).build()
  implicit val mess = messages(application)

  "OffshoreWhichYearsServiceSpec" - {

    val service = new OffshoreWhichYearsServiceImpl(FakeTimeService(2022))

    "must create a checkbox for none of these years row" in {
      val checkbox = service.createDeliberatePriorToCheckbox(19, taxYear2022)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${taxYear2022.back(19).startYear}"))
      checkbox.id mustEqual Some("value_19")
      checkbox.name mustEqual Some("value[19]")
      checkbox.value mustEqual "deliberatePriorTo"
    }

    "must create a checkbox for any prior to 8 years row" in {
      val checkbox = service.createCarelessPriorToCheckbox(8, taxYear2022)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${taxYear2022.back(8).startYear}"))
      checkbox.id mustEqual Some("value_8")
      checkbox.name mustEqual Some("value[8]")
      checkbox.value mustEqual "carelessPriorTo"
    }

    "must create a checkbox for any prior to 6 years row" in {
      val checkbox = service.createReasonableExcusePriorToCheckbox(6, taxYear2022)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${taxYear2022.back(6).startYear}"))
      checkbox.id mustEqual Some("value_6")
      checkbox.name mustEqual Some("value[6]")
      checkbox.value mustEqual "reasonableExcusePriorTo"
    }

    "must create checkbox items for 19 years" in {
      val checkboxItems = service.createYearCheckboxes(19, taxYear2022)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${taxYear2022.startYear - (index+1)}", s"${taxYear2022.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (taxYear2022.startYear - (index+1)).toString
      } 
    }

    "must create checkbox items for 6 years" in {
      val checkboxItems = service.createYearCheckboxes(6, taxYear2022)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${taxYear2022.startYear - (index+1)}", s"${taxYear2022.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (taxYear2022.startYear - (index+1)).toString
      } 
    }

    "must create checkbox items for 8 years" in {
      val checkboxItems = service.createYearCheckboxes(8, taxYear2022)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${taxYear2022.startYear - (index+1)}", s"${taxYear2022.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (taxYear2022.startYear - (index+1)).toString
      } 
    }

  }

  "for tax year 2022/2023" - {

    val service = new OffshoreWhichYearsServiceImpl(FakeTimeService(2022))

    "must create checkbox items for 19 years with none of these years row" in {
      val checkboxItems = service.checkboxItems(Behaviour.Deliberate)
      val first19Elements = checkboxItems.slice(0,18)

      first19Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (taxYear2022.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "deliberatePriorTo"
    }

    "must create checkbox items for careless behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.Careless)
      val expectedList = 
        List(
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "2015",
          "2014",
          "2013",
          "carelessPriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }

    "must create checkbox items for reasonable excuse behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.ReasonableExcuse)
      val expectedList = 
        List(
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "2015",
          "reasonableExcusePriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }
  }

  "for tax year 2023/2024" - {

    val service = new OffshoreWhichYearsServiceImpl(FakeTimeService(2023))

    "must create checkbox items for 19 years with none of these years row" in {
      val checkboxItems = service.checkboxItems(Behaviour.Deliberate)
      val first19Elements = checkboxItems.slice(0,18)

      first19Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (taxYear2023.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "deliberatePriorTo"
    }

    "must create checkbox items for careless behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.Careless)
      val expectedList = 
        List(
          "2022",
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "2015",
          "2014",
          "2013",
          "carelessPriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }

    "must create checkbox items for reasonable excuse behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.ReasonableExcuse)
      val expectedList = 
        List(
          "2022",
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "2015",
          "reasonableExcusePriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }
  }

  "for tax year 2028/2029" - {

    val service = new OffshoreWhichYearsServiceImpl(FakeTimeService(2028))

    "must create checkbox items for 19 years with none of these years row" in {
      val checkboxItems = service.checkboxItems(Behaviour.Deliberate)
      val first19Elements = checkboxItems.slice(0,18)

      first19Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (taxYear2028.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "deliberatePriorTo"
    }

    "must create checkbox items for careless behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.Careless)
      val expectedList = 
        List(
          "2027",
          "2026",
          "2025",
          "2024",
          "2023",
          "2022",
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "carelessPriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }

    "must create checkbox items for reasonable excuse behaviour" in {
      val checkboxItems = service.checkboxItems(Behaviour.ReasonableExcuse)
      val expectedList = 
        List(
          "2027",
          "2026",
          "2025",
          "2024",
          "2023",
          "2022",
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "reasonableExcusePriorTo"
        )

      checkboxItems.map(_.value) mustEqual expectedList
    }

  }
}
