package viewmodels.checkAnswers

import controllers.notification.routes
import models.{CheckMode, UserAnswers}
import pages.WhatIsTheLLPNamePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object WhatIsTheLLPNameSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhatIsTheLLPNamePage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "whatIsTheLLPName.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhatIsTheLLPNameController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("whatIsTheLLPName.change.hidden"))
          )
        )
    }
}
