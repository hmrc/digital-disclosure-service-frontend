package pages

import pages.behaviours.PageBehaviours


class WhatWasTheTypeOfMortgagePageSpec extends PageBehaviours {

  "WhatWasTheTypeOfMortgagePage" - {

    beRetrievable[String](WhatWasTheTypeOfMortgagePage)

    beSettable[String](WhatWasTheTypeOfMortgagePage)

    beRemovable[String](WhatWasTheTypeOfMortgagePage)
  }
}
