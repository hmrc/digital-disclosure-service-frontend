package viewmodels.checkAnswers

import controllers.letting.routes
import models.{CheckMode, UserAnswers}
import pages.DidTheLettingAgentCollectRentOnYourBehalfPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DidTheLettingAgentCollectRentOnYourBehalfSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DidTheLettingAgentCollectRentOnYourBehalfPage).map {
      answer =>

        val value = if (answer) "didTheLettingAgentCollectRentOnYourBehalf.yes" else "didTheLettingAgentCollectRentOnYourBehalf.no"

        SummaryListRowViewModel(
          key     = "didTheLettingAgentCollectRentOnYourBehalf.checkYourAnswersLabel",
          value   = ValueViewModel(value),
          actions = Seq(
            ActionItemViewModel("site.change", routes.DidTheLettingAgentCollectRentOnYourBehalfController.onPageLoad(0, CheckMode).url)
              .withVisuallyHiddenText(messages("didTheLettingAgentCollectRentOnYourBehalf.change.hidden"))
          )
        )
    }
}
