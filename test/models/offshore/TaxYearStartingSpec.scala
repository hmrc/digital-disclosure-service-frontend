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

package models

import base.SpecBase

class TaxYearStartingSpec extends SpecBase {

  "WhichYearsSpec" - {

    "For an empty list of tax years, we should return Nil" in {
      val list = Nil
      TaxYearStarting.findMissingYears(list) mustEqual Nil
    }

    "For a list of 1 tax year, we should return Nil" in {
      val list = List(TaxYearStarting(2012))
      TaxYearStarting.findMissingYears(list) mustEqual Nil
    }

    "For a list of 2 tax years sequential to each other, we should return Nil" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2013))
      TaxYearStarting.findMissingYears(list) mustEqual Nil
    }

    "For a list of 10 tax years sequential to each other, we should return Nil" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2013), TaxYearStarting(2014), TaxYearStarting(2015), TaxYearStarting(2016), TaxYearStarting(2017), TaxYearStarting(2018), TaxYearStarting(2019), TaxYearStarting(2020), TaxYearStarting(2021))
      TaxYearStarting.findMissingYears(list) mustEqual Nil
    }

    "For a list of 2 tax years which have a gap of one year between, that year should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2014))
      TaxYearStarting.findMissingYears(list) mustEqual List(TaxYearStarting(2013))
    }

    "For a list of 2 tax years which have a gap of five years between, those gap years should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2018))
      val result = List(TaxYearStarting(2013), TaxYearStarting(2014), TaxYearStarting(2015), TaxYearStarting(2016), TaxYearStarting(2017)).sorted
      TaxYearStarting.findMissingYears(list) mustEqual result
    }

    "For a list of 3 tax years which have two gaps of one year, those gap years should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2014), TaxYearStarting(2016))
      val result = List(TaxYearStarting(2013), TaxYearStarting(2015)).sorted
      TaxYearStarting.findMissingYears(list) mustEqual result
    }

    "For a list of 3 tax years which have two gaps of five years, those gap years should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2018), TaxYearStarting(2024))
      val result = List(TaxYearStarting(2013), TaxYearStarting(2014), TaxYearStarting(2015), TaxYearStarting(2016), TaxYearStarting(2017), TaxYearStarting(2019), TaxYearStarting(2020), TaxYearStarting(2021), TaxYearStarting(2022), TaxYearStarting(2023)).sorted
      TaxYearStarting.findMissingYears(list) mustEqual result
    }

    "For a list of 10 tax years which have a single gap of one year, that year should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2013), TaxYearStarting(2014), TaxYearStarting(2016), TaxYearStarting(2017), TaxYearStarting(2018), TaxYearStarting(2019), TaxYearStarting(2020), TaxYearStarting(2021), TaxYearStarting(2022))
      TaxYearStarting.findMissingYears(list) mustEqual List(TaxYearStarting(2015))
    }

    "For a list of 10 tax years in reverse order which have a single gap of one year, that year should be returned" in {
      val list = List(TaxYearStarting(2012), TaxYearStarting(2013), TaxYearStarting(2014), TaxYearStarting(2016), TaxYearStarting(2017), TaxYearStarting(2018), TaxYearStarting(2019), TaxYearStarting(2020), TaxYearStarting(2021), TaxYearStarting(2022))
      TaxYearStarting.findMissingYears(list.sorted) mustEqual List(TaxYearStarting(2015))
    }

  }
}
