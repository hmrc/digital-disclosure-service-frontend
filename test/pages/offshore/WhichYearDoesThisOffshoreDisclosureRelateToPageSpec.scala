package pages

import models.WhichYearDoesThisOffshoreDisclosureRelateTo
import pages.behaviours.PageBehaviours

class WhichYearDoesThisOffshoreDisclosureRelateToPageSpec extends PageBehaviours {

  "WhichYearDoesThisOffshoreDisclosureRelateToPage" - {

    beRetrievable[Set[WhichYearDoesThisOffshoreDisclosureRelateTo]](WhichYearDoesThisOffshoreDisclosureRelateToPage)

    beSettable[Set[WhichYearDoesThisOffshoreDisclosureRelateTo]](WhichYearDoesThisOffshoreDisclosureRelateToPage)

    beRemovable[Set[WhichYearDoesThisOffshoreDisclosureRelateTo]](WhichYearDoesThisOffshoreDisclosureRelateToPage)
  }
}
