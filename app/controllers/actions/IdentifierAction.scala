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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import models._

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    import Retrievals._

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised(Agent).retrieve(internalId and allEnrolments){
      case internalId ~ enrolments =>
        val customerId = getAgentCustomerId(enrolments)
        processAuthorisation(internalId, request, block, true, customerId)
    }
    .recoverWith {_ => authorised(Organisation or ConfidenceLevel.L250).retrieve(internalId and allEnrolments){
      case internalId ~ enrolments =>
        val customerId = getCustomerId(enrolments)
        processAuthorisation(internalId, request, block, false, customerId)
    }}
    .recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: InsufficientConfidenceLevel =>
        Redirect(config.identityVerificationURL)
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }

  def getAgentCustomerId(enrolments: Enrolments): Option[CustomerId] = {
    enrolments.getEnrolment("HMRC-AS-AGENT").flatMap(_.getIdentifier("AgentReferenceNumber").map(enrol => ARN(enrol.value)))
  }

  def getCustomerId(enrolments: Enrolments): Option[CustomerId] = {
    val ninoOpt: Option[NINO] = enrolments.getEnrolment("HMRC-NI").flatMap(_.getIdentifier("nino").map(enrol => NINO(enrol.value)))
    val sautrOpt: Option[SAUTR] = enrolments.getEnrolment("IR-SA").flatMap(_.getIdentifier("UTR").map(enrol => SAUTR(enrol.value)))
    val ctutrOpt: Option[CAUTR] = enrolments.getEnrolment("IR-CT").flatMap(_.getIdentifier("UTR").map(enrol => CAUTR(enrol.value)))
    List(ninoOpt, sautrOpt, ctutrOpt).flatten.headOption
  }

  def processAuthorisation[A](internalIdOpt: Option[String], request: Request[A], block: IdentifierRequest[A] => Future[Result], isAgent: Boolean, customerId: Option[CustomerId])(implicit hc: HeaderCarrier) = {
    val sessionId = hc.sessionId.fold("-")(_.value)
    internalIdOpt.map {
      internalId => block(IdentifierRequest(request, internalId, sessionId, isAgent, customerId))
    }.getOrElse(throw new UnauthorizedException("Unable to retrieve internal Id"))
  }

}