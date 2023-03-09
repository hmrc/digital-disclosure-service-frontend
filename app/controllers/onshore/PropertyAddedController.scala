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

package controllers.onshore

import controllers.actions._
import forms.PropertyAddedFormProvider

import javax.inject.Inject
import models.{LettingProperty, Mode, UserAnswers}
import navigation.OnshoreNavigator
import pages.{LettingPropertyPage, PropertyAddedPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.onshore.LettingPropertyModel
import views.html.onshore.PropertyAddedView

import scala.concurrent.{ExecutionContext, Future}

class PropertyAddedController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: OnshoreNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: PropertyAddedFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: PropertyAddedView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      getProperties(request.userAnswers, mode) match {
        case properties if properties.nonEmpty => Ok(view(form, properties, mode))
        case _ => Redirect(controllers.letting.routes.RentalAddressLookupController.lookupAddress(0, mode).url)
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val properties = getProperties(request.userAnswers, mode)

      form.bindFromRequest().fold(
        formWithErrors =>

          Future.successful(BadRequest(view(formWithErrors, properties, mode))),

        value =>

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PropertyAddedPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PropertyAddedPage, mode, updatedAnswers))
      )
  }

   def remove(i:Int, mode:Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
      implicit request =>

        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.removeBySeqIndex[LettingProperty](LettingPropertyPage, i))
          _ <- sessionService.set(updatedAnswers)
        } yield {
          updatedAnswers.get(LettingPropertyPage) match {
            case Some(properties) if properties.nonEmpty => Redirect(routes.PropertyAddedController.onSubmit(mode).url)
            case _ =>  Redirect(controllers.letting.routes.RentalAddressLookupController.lookupAddress(0, mode).url)
          }
        }

    }



  private def getProperties(userAnswers: UserAnswers, mode:Mode)(implicit messages:Messages): Seq[SummaryListRow] =
    userAnswers.get(LettingPropertyPage) match {
      case Some(value) => LettingPropertyModel.row(value, mode)
      case _ => Seq()
    }
}
