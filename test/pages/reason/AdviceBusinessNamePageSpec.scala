package pages

import pages.behaviours.PageBehaviours


class AdviceBusinessNamePageSpec extends PageBehaviours {

  "AdviceBusinessNamePage" - {

    beRetrievable[String](AdviceBusinessNamePage)

    beSettable[String](AdviceBusinessNamePage)

    beRemovable[String](AdviceBusinessNamePage)
  }
}
