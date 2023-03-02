package pages

import pages.behaviours.PageBehaviours

class PropertyAddedPageSpec extends PageBehaviours {

  "PropertyAddedPage" - {

    beRetrievable[Boolean](PropertyAddedPage)

    beSettable[Boolean](PropertyAddedPage)

    beRemovable[Boolean](PropertyAddedPage)
  }
}
