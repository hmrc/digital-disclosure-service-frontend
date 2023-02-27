package pages

import pages.behaviours.PageBehaviours

class WasALettingAgentUsedToManagePropertyPageSpec extends PageBehaviours {

  "WasALettingAgentUsedToManagePropertyPage" - {

    beRetrievable[Boolean](WasALettingAgentUsedToManagePropertyPage)

    beSettable[Boolean](WasALettingAgentUsedToManagePropertyPage)

    beRemovable[Boolean](WasALettingAgentUsedToManagePropertyPage)
  }
}
