package pages

import pages.behaviours.PageBehaviours

class RegisteredAddressInUKPageSpec extends PageBehaviours {

  "RegisteredAddressInUKPage" - {

    beRetrievable[Boolean](RegisteredAddressInUKPage)

    beSettable[Boolean](RegisteredAddressInUKPage)

    beRemovable[Boolean](RegisteredAddressInUKPage)
  }
}
