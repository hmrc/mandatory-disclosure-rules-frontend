/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{AffinityType, CheckMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class ContactDetailsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  val navigator: ContactDetailsNavigator = new ContactDetailsNavigator

  "Navigator" - {

    val organisation = AffinityType.toAffinityTypes("Organisation")
    val individual   = AffinityType.toAffinityTypes("Individual")

    "in Check mode" - {

      "must go from Contact Phone page to Change Individual Details page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContactPhonePage, individual, CheckMode, answers)
              .mustBe(routes.ChangeIndividualContactDetailsController.onPageLoad())
        }
      }

      "must go from Contact Name page to Contact Email page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContactNamePage, individual, CheckMode, answers)
              .mustBe(routes.ContactEmailController.onPageLoad())
        }
      }

      "must go from Have Telephone page to Contact Phone Individual page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(HaveTelephonePage, true).success.value

            navigator
              .nextPage(HaveTelephonePage, individual, CheckMode, updatedAnswers)
              .mustBe(routes.ContactPhoneIndividualController.onPageLoad())
        }
      }

      "must go from Contact Phone page to Change Organisation Details page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContactPhonePage, organisation, CheckMode, answers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }

      "must go from Have Telephone Page to Change Organisation Details page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers.set(HaveTelephonePage, false).success.value

            navigator
              .nextPage(ContactPhonePage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }

      "must go from Contact Name page to Contact Email Organisation page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContactNamePage, organisation, CheckMode, answers)
              .mustBe(routes.ContactEmailOrganisationController.onPageLoad())
        }
      }

      "must go from Contact Email page to Have Telephone page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContactEmailPage, organisation, CheckMode, answers)
              .mustBe(routes.HaveTelephoneController.onPageLoad(organisation))
        }
      }

      "must go from Have Second Contact page to Second Contact Name page when 'YES' is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers.set(HaveSecondContactPage, true).success.value

            navigator
              .nextPage(HaveSecondContactPage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.SecondContactNameController.onPageLoad())
        }
      }

      "must go from Have Second Contact page to Change Organisation Details page when 'NO' is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers.set(HaveSecondContactPage, false).success.value

            navigator
              .nextPage(HaveSecondContactPage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }

      "must go from Second Contact Name page to Second Contact Email page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(SecondContactNamePage, organisation, CheckMode, answers)
              .mustBe(routes.SecondContactEmailController.onPageLoad())
        }
      }

      "must go from Second Contact Email page to Second Contact Have Phone page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(SecondContactEmailPage, organisation, CheckMode, answers)
              .mustBe(routes.SecondContactHavePhoneController.onPageLoad())
        }
      }

      "must go from Second Contact Have Phone page to Second Contact Phone page when 'YES' is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers.set(SecondContactHavePhonePage, true).success.value

            navigator
              .nextPage(SecondContactHavePhonePage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.SecondContactPhoneController.onPageLoad())
        }
      }

      "must go from Second Contact Have Phone page to Change Organisation Details page when 'NO' is selected" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers =
              answers.set(SecondContactHavePhonePage, false).success.value

            navigator
              .nextPage(SecondContactHavePhonePage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }

      "must go from Second Contact Phone page to Change Organisation Details page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(SecondContactPhonePage, organisation, CheckMode, answers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }

      "must go from Have Telephone page to Contact Phone Organisation page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(HaveTelephonePage, true).success.value

            navigator
              .nextPage(HaveTelephonePage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.ContactPhoneOrganisationController.onPageLoad())
        }
      }

      "must go from Have Telephone page to Contact Phone Organisation page if phone not set" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.set(HaveTelephonePage, false).success.value

            navigator
              .nextPage(HaveTelephonePage, organisation, CheckMode, updatedAnswers)
              .mustBe(routes.ChangeOrganisationContactDetailsController.onPageLoad())
        }
      }
    }
  }
}
