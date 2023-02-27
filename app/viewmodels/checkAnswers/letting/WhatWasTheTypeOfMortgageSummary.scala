package viewmodels.checkAnswers

import controllers.letting.routes
import models.{CheckMode, UserAnswers}
import pages.WhatWasTheTypeOfMortgagePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object WhatWasTheTypeOfMortgageSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhatWasTheTypeOfMortgagePage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "whatWasTheTypeOfMortgage.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhatWasTheTypeOfMortgageController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("whatWasTheTypeOfMortgage.change.hidden"))
          )
        )
    }
}
