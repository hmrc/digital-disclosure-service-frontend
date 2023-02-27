package viewmodels.checkAnswers

import controllers.letting.routes
import models.{CheckMode, UserAnswers}
import pages.WasALettingAgentUsedToManagePropertyPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object WasALettingAgentUsedToManagePropertySummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WasALettingAgentUsedToManagePropertyPage).map {
      answer =>

        val value = if (answer) "wasALettingAgentUsedToManageProperty.yes" else "wasALettingAgentUsedToManageProperty.no"

        SummaryListRowViewModel(
          key     = "wasALettingAgentUsedToManageProperty.checkYourAnswersLabel",
          value   = ValueViewModel(value),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(0, CheckMode).url)
              .withVisuallyHiddenText(messages("wasALettingAgentUsedToManageProperty.change.hidden"))
          )
        )
    }
}
