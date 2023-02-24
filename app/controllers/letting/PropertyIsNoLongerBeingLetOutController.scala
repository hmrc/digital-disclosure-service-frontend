package controllers.letting

import controllers.actions._
import forms.PropertyIsNoLongerBeingLetOutFormProvider
import javax.inject.Inject
import models.Mode
import navigation.LettingNavigator
import pages.PropertyIsNoLongerBeingLetOutPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.letting.PropertyIsNoLongerBeingLetOutView

import scala.concurrent.{ExecutionContext, Future}

class PropertyIsNoLongerBeingLetOutController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: LettingNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: PropertyIsNoLongerBeingLetOutFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: PropertyIsNoLongerBeingLetOutView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PropertyIsNoLongerBeingLetOutPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PropertyIsNoLongerBeingLetOutPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PropertyIsNoLongerBeingLetOutPage, mode, updatedAnswers))
      )
  }
}
