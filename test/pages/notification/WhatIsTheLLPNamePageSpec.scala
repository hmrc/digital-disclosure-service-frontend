package pages

import pages.behaviours.PageBehaviours


class WhatIsTheLLPNamePageSpec extends PageBehaviours {

  "WhatIsTheLLPNamePage" - {

    beRetrievable[String](WhatIsTheLLPNamePage)

    beSettable[String](WhatIsTheLLPNamePage)

    beRemovable[String](WhatIsTheLLPNamePage)
  }
}
