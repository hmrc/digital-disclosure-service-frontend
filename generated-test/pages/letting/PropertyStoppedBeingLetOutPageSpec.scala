package pages

import pages.behaviours.PageBehaviours

class PropertyStoppedBeingLetOutPageSpec extends PageBehaviours {

  "PropertyStoppedBeingLetOutPage" - {

    beRetrievable[Boolean](PropertyStoppedBeingLetOutPage)

    beSettable[Boolean](PropertyStoppedBeingLetOutPage)

    beRemovable[Boolean](PropertyStoppedBeingLetOutPage)
  }
}
