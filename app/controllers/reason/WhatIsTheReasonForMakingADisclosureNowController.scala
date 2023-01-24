package controllers.reason

import controllers.actions._
import forms.WhatIsTheReasonForMakingADisclosureNowFormProvider
import javax.inject.Inject
import models.Mode
import navigation.ReasonNavigator
import pages.WhatIsTheReasonForMakingADisclosureNowPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reason.WhatIsTheReasonForMakingADisclosureNowView

import scala.concurrent.{ExecutionContext, Future}

class WhatIsTheReasonForMakingADisclosureNowController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: ReasonNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhatIsTheReasonForMakingADisclosureNowFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhatIsTheReasonForMakingADisclosureNowView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsTheReasonForMakingADisclosureNowPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsTheReasonForMakingADisclosureNowPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatIsTheReasonForMakingADisclosureNowPage, mode, updatedAnswers))
      )
  }
}
