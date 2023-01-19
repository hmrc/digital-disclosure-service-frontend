package pages

import pages.behaviours.PageBehaviours


class CountryOfYourOffshoreLiabilityPageSpec extends PageBehaviours {

  "CountryOfYourOffshoreLiabilityPage" - {

    beRetrievable[String](CountryOfYourOffshoreLiabilityPage)

    beSettable[String](CountryOfYourOffshoreLiabilityPage)

    beRemovable[String](CountryOfYourOffshoreLiabilityPage)
  }
}
