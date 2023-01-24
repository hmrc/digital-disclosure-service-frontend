package pages

import pages.behaviours.PageBehaviours


class WhatIsTheReasonForMakingADisclosureNowPageSpec extends PageBehaviours {

  "WhatIsTheReasonForMakingADisclosureNowPage" - {

    beRetrievable[String](WhatIsTheReasonForMakingADisclosureNowPage)

    beSettable[String](WhatIsTheReasonForMakingADisclosureNowPage)

    beRemovable[String](WhatIsTheReasonForMakingADisclosureNowPage)
  }
}
