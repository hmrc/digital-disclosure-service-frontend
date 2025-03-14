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
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.MakingNilDisclosureView
import models.{Behaviour, RelatesTo, UserAnswers}
import models.WhyAreYouMakingThisDisclosure._
import pages.{RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import services.OffshoreWhichYearsService

class MakingNilDisclosureController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: MakingNilDisclosureView,
  offshoreWhichYearsService: OffshoreWhichYearsService
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
    val year   = offshoreWhichYearsService.getEarliestYearByBehaviour(getBehaviour(request.userAnswers)).toString

    Ok(view(request.userAnswers.isTheUserTheIndividual, entity, year))
  }

  def getBehaviour(ua: UserAnswers): Behaviour =
    ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value)
          if value.contains(DidNotNotifyNoExcuse) ||
            value.contains(DeliberatelyDidNotNotify) ||
            value.contains(DeliberateInaccurateReturn) ||
            value.contains(DeliberatelyDidNotFile) =>
        Behaviour.Deliberate
      case Some(value) if value.contains(InaccurateReturnNoCare) => Behaviour.Careless
      case _                                                     => Behaviour.ReasonableExcuse
    }

}
