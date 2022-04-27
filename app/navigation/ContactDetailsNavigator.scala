/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import controllers.routes
import models.{AffinityType, _}
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class ContactDetailsNavigator @Inject() () {

  val normalRoutes: (Page, AffinityType) => UserAnswers => Call = {
    case (ContactEmailPage, _) => _ => routes.IndexController.onPageLoad //TODO GOTO Telephone
    case _                     => _ => routes.IndexController.onPageLoad
  }

  val checkRouteMap: (Page, AffinityType) => UserAnswers => Call = {
    case (ContactPhonePage, Individual)   => _ => routes.ChangeIndividualContactDetailsController.onPageLoad()
    case (ContactPhonePage, Organisation) => _ => routes.ChangeOrganisationContactDetailsController.onPageLoad()
    case (HaveTelephonePage, affinity)    => ua => haveTelephoneRoutes(CheckMode, affinity)(ua)
    case (ContactNamePage, affinity)      => _ => routes.ContactEmailController.onPageLoad(affinity)
    case (ContactEmailPage, affinity)     => _ => routes.HaveTelephoneController.onPageLoad(affinity)
    case (HaveSecondContactPage, Organisation) =>
      ua =>
        yesNoPage(
          ua,
          HaveSecondContactPage,
          routes.SecondContactNameController.onPageLoad(),
          routes.ChangeOrganisationContactDetailsController.onPageLoad()
        )
    case (SecondContactNamePage, Organisation)  => _ => routes.SecondContactEmailController.onPageLoad()
    case (SecondContactEmailPage, Organisation) => _ => routes.SecondContactHavePhoneController.onPageLoad()
    case (SecondContactHavePhonePage, Organisation) =>
      ua =>
        yesNoPage(
          ua,
          SecondContactHavePhonePage,
          routes.SecondContactPhoneController.onPageLoad(),
          routes.ChangeOrganisationContactDetailsController.onPageLoad()
        )
    case (SecondContactPhonePage, Organisation) => _ => routes.ChangeOrganisationContactDetailsController.onPageLoad()
    case _                                      => _ => routes.ThereIsAProblemController.onPageLoad()
  }

  private def haveTelephoneRoutes(mode: Mode, affinityType: AffinityType)(ua: UserAnswers): Call =
    ua.get(HaveTelephonePage) match {
      case Some(hasPhone) if hasPhone =>
        routes.ContactPhoneController.onPageLoad(affinityType)
      case _ =>
        nextPage(ContactPhonePage, affinityType, mode, ua)
    }

  def yesNoPage(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call =
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(routes.ThereIsAProblemController.onPageLoad())

  def nextPage(page: Page, affinityType: AffinityType, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page, affinityType)(userAnswers)
    case CheckMode =>
      checkRouteMap(page, affinityType)(userAnswers)
  }

}
