package pages

import pages.behaviours.PageBehaviours

class DidTheLettingAgentCollectRentOnYourBehalfPageSpec extends PageBehaviours {

  "DidTheLettingAgentCollectRentOnYourBehalfPage" - {

    beRetrievable[Boolean](DidTheLettingAgentCollectRentOnYourBehalfPage)

    beSettable[Boolean](DidTheLettingAgentCollectRentOnYourBehalfPage)

    beRemovable[Boolean](DidTheLettingAgentCollectRentOnYourBehalfPage)
  }
}
