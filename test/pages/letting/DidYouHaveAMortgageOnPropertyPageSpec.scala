package pages

import pages.behaviours.PageBehaviours

class DidYouHaveAMortgageOnPropertyPageSpec extends PageBehaviours {

  "DidYouHaveAMortgageOnPropertyPage" - {

    beRetrievable[Boolean](DidYouHaveAMortgageOnPropertyPage)

    beSettable[Boolean](DidYouHaveAMortgageOnPropertyPage)

    beRemovable[Boolean](DidYouHaveAMortgageOnPropertyPage)
  }
}
