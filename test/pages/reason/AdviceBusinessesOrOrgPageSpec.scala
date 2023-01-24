package pages

import pages.behaviours.PageBehaviours

class AdviceBusinessesOrOrgPageSpec extends PageBehaviours {

  "AdviceBusinessesOrOrgPage" - {

    beRetrievable[Boolean](AdviceBusinessesOrOrgPage)

    beSettable[Boolean](AdviceBusinessesOrOrgPage)

    beRemovable[Boolean](AdviceBusinessesOrOrgPage)
  }
}
