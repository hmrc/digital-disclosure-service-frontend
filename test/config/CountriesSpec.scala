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

package config

import base.SpecBase
import com.typesafe.config.ConfigException
import org.mockito.ArgumentMatchers.refEq
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.running
import play.api.{Application, Environment}
import play.api.test.Injecting

import java.io.InputStream

class CountriesSpec extends SpecBase with Injecting with BeforeAndAfterEach with MockitoSugar {

  implicit lazy val app: Application = applicationBuilder().build()

  val mockEnv = mock[Environment]
  lazy val env: Environment = inject[Environment]

  override protected def beforeEach(): Unit = {
    reset(mockEnv)
    mockActualEnvFile("location-autocomplete-canonical-list.json")
    mockActualEnvFile("ISO_3166-alpha3-alpha2-numeric.json")
    super.beforeEach()
  }

  "Countries" - {
    "must throw an exception when the canonical list file is not found" in {

      when(mockEnv.resourceAsStream(refEq("location-autocomplete-canonical-list.json"))).thenReturn(None)
      val caught = intercept[ConfigException.BadValue] {
        new Countries(mockEnv)
      }

      running(app) {
        caught.getMessage mustEqual "Invalid value at 'location-autocomplete-canonical-list.json': Alpha2 to Name map cannot be constructed."
      }
    }

    "must throw an exception when the iso3 file is not found" in {
      when(mockEnv.resourceAsStream(refEq("ISO_3166-alpha3-alpha2-numeric.json"))).thenReturn(None)
      val caught = intercept[ConfigException.BadValue] {
        new Countries(mockEnv)
      }
      running(app) {
        caught.getMessage mustEqual "Invalid value at 'ISO_3166-alpha3-alpha2-numeric.json': ISO codes json does not exist"
      }
    }
  }

  def mockActualEnvFile(filename: String): OngoingStubbing[Option[InputStream]] =
    when(mockEnv.resourceAsStream(refEq(filename))).thenReturn(env.resourceAsStream(filename))

}
