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

import config.Country
import controllers.actions._
import forms.CountryOfYourOffshoreLiabilityFormProvider

import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.OffshoreNavigator
import pages.CountryOfYourOffshoreLiabilityPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.CountryOfYourOffshoreLiabilityView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CountryOfYourOffshoreLiabilityController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OffshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: CountryOfYourOffshoreLiabilityFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CountryOfYourOffshoreLiabilityView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  def onPageLoad(index:Option[Int] = None, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val form: Form[Set[Country]] = formProvider()

      val country: Option[Country] = index.flatMap(i => request.userAnswers.getByIndex(CountryOfYourOffshoreLiabilityPage, i))

      val preparedForm = country match {
        case None => form
        case Some(value) => form.fill(Set(value))
      }

      Ok(view(index, preparedForm, mode))
  }

  def onSubmit(index:Option[Int] = None, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val form: Form[Set[Country]] = formProvider()

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(index, formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(setOrAdd(request.userAnswers, index, value.head))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfYourOffshoreLiabilityPage, mode, updatedAnswers))
      )
  }

  private def setOrAdd(userAnswers:UserAnswers, index:Option[Int], value:Country): Try[UserAnswers] = index match {
    case Some(indexValue) => userAnswers.setByIndex[Country](CountryOfYourOffshoreLiabilityPage, indexValue, value)
    case _ => userAnswers.addToSet[Country](CountryOfYourOffshoreLiabilityPage, value)
  }

}
