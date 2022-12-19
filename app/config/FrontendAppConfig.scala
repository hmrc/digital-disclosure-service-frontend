/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

import scala.concurrent.duration.{DurationInt, FiniteDuration}

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  lazy val host: String    = configuration.get[String]("self.url")
  lazy val appName: String = configuration.get[String]("appName")

  private lazy val contactFormServiceIdentifier = "digital-disclosure-service-frontend"

  def feedbackUrl(implicit request: RequestHeader): String =
    s"/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val signOutUrl: String       = configuration.get[String]("urls.signOut")

  private lazy val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  lazy val exitSurveyUrl: String             = s"$exitSurveyBaseUrl/feedback/digital-disclosure-service-frontend"

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  lazy val fullDisclosureJourneyEnabled: Boolean =
    configuration.get[Boolean]("features.full-disclosure-journey")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en")
  )

  lazy val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val identityVerificationURL: String = {
    val identityVerificationFrontendBaseUrl: String = configuration.get[String]("identity-verification-frontend.url")
    val upliftUri: String = configuration.get[String]("identity-verification-frontend.uplift-uri")
    val origin: String = configuration.get[String]("identity-verification-frontend.origin")
    val confidenceLevel: Int = configuration.get[Int]("identity-verification-frontend.target-confidence-level")
    val successUrl: String = host + controllers.routes.IndexController.onPageLoad.url
    val failureUrl: String = host + controllers.routes.UnauthorisedController.onPageLoad.url

    s"$identityVerificationFrontendBaseUrl$upliftUri?origin=$origin&confidenceLevel=$confidenceLevel&completionURL=$successUrl&failureURL=$failureUrl"
  }

  lazy val retryIntervals: Seq[FiniteDuration] = configuration
    .get[Seq[Int]]("retry-intervals")
    .map(_.milliseconds)
    .toList
  
}
