package pages

import pages.behaviours.PageBehaviours


class WhichLandlordAssociationsAreYouAMemberOfPageSpec extends PageBehaviours {

  "WhichLandlordAssociationsAreYouAMemberOfPage" - {

    beRetrievable[String](WhichLandlordAssociationsAreYouAMemberOfPage)

    beSettable[String](WhichLandlordAssociationsAreYouAMemberOfPage)

    beRemovable[String](WhichLandlordAssociationsAreYouAMemberOfPage)
  }
}
