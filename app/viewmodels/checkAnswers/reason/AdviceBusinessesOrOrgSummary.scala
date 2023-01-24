package viewmodels.checkAnswers

import controllers.reason.routes
import models.{CheckMode, UserAnswers}
import pages.AdviceBusinessesOrOrgPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object AdviceBusinessesOrOrgSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AdviceBusinessesOrOrgPage).map {
      answer =>

        val value = if (answer) "site.yes" else "site.no"

        SummaryListRowViewModel(
          key     = "adviceBusinessesOrOrg.checkYourAnswersLabel",
          value   = ValueViewModel(value),
          actions = Seq(
            ActionItemViewModel("site.change", routes.AdviceBusinessesOrOrgController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("adviceBusinessesOrOrg.change.hidden"))
          )
        )
    }
}
