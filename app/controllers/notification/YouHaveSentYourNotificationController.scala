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

package controllers.notification

import config.FrontendAppConfig
import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.YouHaveSentYourNotificationView
import pages._
import models.{UserAnswers, AreYouTheEntity}


class YouHaveSentYourNotificationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredActionEvenSubmitted,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: YouHaveSentYourNotificationView
                                     )(implicit val appConfig: FrontendAppConfig) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val caseReferenceExists = request.userAnswers.get(LetterReferencePage).isDefined
      val isTheEntity = isTheUserTheEntity(request.userAnswers)
      val submittedReference = request.userAnswers.get(LetterReferencePage).getOrElse("")
      val generatedRef = request.userAnswers.metadata.reference.getOrElse("")

      val reference = if (caseReferenceExists) submittedReference else generatedRef

      Ok(view(caseReferenceExists, reference, isTheEntity, request.userAnswers.isDisclosure))
  }

  def isTheUserTheEntity(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheEntityPage).map(_ == AreYouTheEntity.YesIAm).getOrElse(true)
  }

  
}
