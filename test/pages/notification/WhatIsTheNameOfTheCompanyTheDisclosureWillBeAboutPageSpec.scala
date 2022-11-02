package pages

import pages.behaviours.PageBehaviours


class WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPageSpec extends PageBehaviours {

  "WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage" - {

    beRetrievable[String](WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage)

    beSettable[String](WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage)

    beRemovable[String](WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage)
  }
}
