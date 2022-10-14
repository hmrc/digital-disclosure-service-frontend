package viewmodels.checkAnswers

import base.SpecBase

class OnshoreLiabilitiesSummarySpec extends SpecBase {

  "OnshoreLiabilitiesSummary.row" - {
    "must return a row where the user selects No for offshore liabilities" - {
      val ua = UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IDoNotWantTo).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

    "must return a row where the user selects Yes for offshore liabilities and selects Yes for onshore liabilities" - {
      val ua = (for {
        offAnswer <- UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
        onAnswer <- offAnswer.set(OnshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
      } yield onAnswer).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

    "must return a row where the user selects Yes for offshore liabilities and selects No for onshore liabilities" - {
      val ua = (for {
        offAnswer <- UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
        onAnswer <- offAnswer.set(OnshoreLiabilitiesPage, OffshoreLiabilities.IDoNotWantTo)
      } yield onAnswer).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

  }
  
}
