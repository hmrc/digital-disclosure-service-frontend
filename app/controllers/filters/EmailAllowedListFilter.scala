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

package controllers.filters

import javax.inject.Singleton
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.Configuration
import play.api.mvc.Results.Redirect
import play.api.mvc.{Filter, RequestHeader, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import controllers.routes
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import play.api.Logging
import scala.collection.JavaConverters._

import java.util.Locale
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailAllowedListFilter @Inject() (
  val mat: Materializer,
  val authConnector: AuthConnector,
  config: Configuration
)(implicit ec: ExecutionContext)
    extends Filter
    with AuthorisedFunctions
    with Logging {

  val userEmailListEnabled: Boolean               = config.underlying.getBoolean("email-allow-list.enabled")
  val userEmailAllowedListLowerCase: List[String] =
    config.underlying.getStringList("email-allow-list.list").asScala.toList.map(_.toLowerCase(Locale.UK))

  private def isExcludedEndpoint(rh: RequestHeader): Boolean =
    rh.path.contains(routes.UnauthorisedController.onPageLoad.url) ||
      rh.path.contains("hmrc-frontend") ||
      rh.path.contains("assets") ||
      rh.path.contains("ping/ping")||
      rh.path.contains("sign-out")

  override def apply(nextFilter: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] =
    if (userEmailListEnabled) {
      if (isExcludedEndpoint(rh)) {
        nextFilter(rh)
      } else {
        implicit val hc: HeaderCarrier =
          HeaderCarrierConverter.fromRequestAndSession(rh, rh.session)
        authorised()
          .retrieve(Retrievals.email) { emailOpt =>

            val isAllowed =
              emailOpt.exists(email => userEmailAllowedListLowerCase.contains(email.toLowerCase(Locale.UK)))

            if (isAllowed) nextFilter(rh) else Future.successful(Redirect(routes.UnauthorisedController.onPageLoad))
          }
          .recoverWith { case _: NoActiveSession => nextFilter(rh) }
      }
    } else {
      nextFilter(rh)
    }
}