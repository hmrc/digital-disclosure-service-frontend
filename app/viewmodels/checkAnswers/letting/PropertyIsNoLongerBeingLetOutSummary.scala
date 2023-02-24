package viewmodels.checkAnswers

import java.time.format.DateTimeFormatter

import controllers.letting.routes
import models.{CheckMode, UserAnswers}
import pages.PropertyIsNoLongerBeingLetOutPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PropertyIsNoLongerBeingLetOutSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PropertyIsNoLongerBeingLetOutPage).map {
      answer =>

        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

        SummaryListRowViewModel(
          key     = "propertyIsNoLongerBeingLetOut.checkYourAnswersLabel",
          value   = ValueViewModel(answer.format(dateFormatter)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("propertyIsNoLongerBeingLetOut.change.hidden"))
          )
        )
    }
}
