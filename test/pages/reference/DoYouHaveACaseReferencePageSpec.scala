package pages

import pages.behaviours.PageBehaviours

class DoYouHaveACaseReferencePageSpec extends PageBehaviours {

  "DoYouHaveACaseReferencePage" - {

    beRetrievable[Boolean](DoYouHaveACaseReferencePage)

    beSettable[Boolean](DoYouHaveACaseReferencePage)

    beRemovable[Boolean](DoYouHaveACaseReferencePage)
  }
}
