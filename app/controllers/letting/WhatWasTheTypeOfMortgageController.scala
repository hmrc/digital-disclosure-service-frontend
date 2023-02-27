package controllers.letting

import controllers.actions._
import forms.WhatWasTheTypeOfMortgageFormProvider
import javax.inject.Inject
import models.Mode
import navigation.LettingNavigator
import pages.WhatWasTheTypeOfMortgagePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.letting.WhatWasTheTypeOfMortgageView

import scala.concurrent.{ExecutionContext, Future}

class WhatWasTheTypeOfMortgageController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: LettingNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhatWasTheTypeOfMortgageFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhatWasTheTypeOfMortgageView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatWasTheTypeOfMortgagePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatWasTheTypeOfMortgagePage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatWasTheTypeOfMortgagePage, mode, updatedAnswers))
      )
  }
}
