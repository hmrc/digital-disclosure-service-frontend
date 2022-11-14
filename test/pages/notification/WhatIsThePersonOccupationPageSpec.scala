package pages

import pages.behaviours.PageBehaviours


class WhatIsThePersonOccupationPageSpec extends PageBehaviours {

  "WhatIsThePersonOccupationPage" - {

    beRetrievable[String](WhatIsThePersonOccupationPage)

    beSettable[String](WhatIsThePersonOccupationPage)

    beRemovable[String](WhatIsThePersonOccupationPage)
  }
}
