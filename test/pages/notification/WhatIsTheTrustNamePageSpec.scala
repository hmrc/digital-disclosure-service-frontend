package pages

import pages.behaviours.PageBehaviours


class WhatIsTheTrustNamePageSpec extends PageBehaviours {

  "WhatIsTheTrustNamePage" - {

    beRetrievable[String](WhatIsTheTrustNamePage)

    beSettable[String](WhatIsTheTrustNamePage)

    beRemovable[String](WhatIsTheTrustNamePage)
  }
}
