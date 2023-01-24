package viewmodels.checkAnswers

import controllers.reason.routes
import models.{CheckMode, UserAnswers}
import pages.WhatIsTheReasonForMakingADisclosureNowPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object WhatIsTheReasonForMakingADisclosureNowSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhatIsTheReasonForMakingADisclosureNowPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "whatIsTheReasonForMakingADisclosureNow.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhatIsTheReasonForMakingADisclosureNowController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("whatIsTheReasonForMakingADisclosureNow.change.hidden"))
          )
        )
    }
}
