package controllers.reason

import controllers.actions._
import forms.AdviceBusinessesOrOrgFormProvider
import javax.inject.Inject
import models.Mode
import navigation.ReasonNavigator
import pages.AdviceBusinessesOrOrgPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reason.AdviceBusinessesOrOrgView

import scala.concurrent.{ExecutionContext, Future}

class AdviceBusinessesOrOrgController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: ReasonNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: AdviceBusinessesOrOrgFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: AdviceBusinessesOrOrgView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AdviceBusinessesOrOrgPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AdviceBusinessesOrOrgPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AdviceBusinessesOrOrgPage, mode, updatedAnswers))
      )
  }
}
