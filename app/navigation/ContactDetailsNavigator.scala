/*
 * Copyright 2021 HM Revenue & Customs
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
    case (ContactEmailPage, _) => _ => routes.IndexController.onPageLoad() //TODO GOTO Telephone
    case _                     => _ => routes.IndexController.onPageLoad()
  }

  val checkRouteMap: (Page, AffinityType) => UserAnswers => Call = {
    case (ContactEmailPage, Individual)   => _ => routes.ChangeIndividualContactDetailsController.onPageLoad()
    case (ContactEmailPage, Organisation) => _ => routes.ChangeOrganisationContactDetailsController.onPageLoad()
    case _                                => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, affinityType: AffinityType, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page, affinityType)(userAnswers)
    case CheckMode =>
      checkRouteMap(page, affinityType)(userAnswers)
  }

}
