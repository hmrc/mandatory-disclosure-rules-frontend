/*
 * Copyright 2023 HM Revenue & Customs
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

package models.subscription

import models.UserAnswers
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages._
import play.api.libs.json.{JsValue, Json}

class RequestDetailForUpdateSpec extends AnyFreeSpec with Matchers with TryValues with OptionValues {

  private val requestDetails: RequestDetailForUpdate = RequestDetailForUpdate(
    IDType = "MDR",
    IDNumber = "IDNumber",
    tradingName = Some("Trading Name"),
    isGBUser = true,
    primaryContact = ContactInformation(IndividualDetails("firstName", Some("middleName"), "lastName"), "test@email.com", Some("+4411223344"), None),
    secondaryContact = Some(ContactInformation(OrganisationDetails("orgName"), "test1@email.com", Some("+3311211212"), None))
  )

  private val responseDetail: ResponseDetail = ResponseDetail(
    "IDNumber",
    Some("Trading Name"),
    isGBUser = true,
    ContactInformation(IndividualDetails("firstName", Some("middleName"), "lastName"), "primaryEmail@email.com", Some("11111111"), None),
    secondaryContact = Some(ContactInformation(OrganisationDetails("orgName"), "secondaryEmail@email.com", Some("22222222"), None))
  )

  "RequestDetail" - {
    "serialise to json" in {

      val expectedJson: JsValue = Json.parse("""
          |{
          |      "IDType": "MDR",
          |      "IDNumber": "IDNumber",
          |      "tradingName": "Trading Name",
          |      "isGBUser": true,
          |      "primaryContact":
          |        {
          |          "individual": {
          |             "lastName": "lastName",
          |             "firstName": "firstName",
          |             "middleName": "middleName"
          |         },
          |          "email": "test@email.com",
          |          "phone": "+4411223344"
          |        },
          |      "secondaryContact":
          |        {
          |          "organisation": {
          |            "organisationName": "orgName"
          |          },
          |          "email": "test1@email.com",
          |          "phone": "+3311211212"
          |        }
          |}
          |""".stripMargin)

      Json.toJson(requestDetails) mustBe expectedJson
    }

    "createRequestDetails" - {

      "must return RequestDetailForUpdate for the input UserAnswer and ResponseDetail" in {

        val userAnswers = UserAnswers("id")
          .set(ContactEmailPage, "test@email.com")
          .success
          .value
          .set(HaveTelephonePage, true)
          .success
          .value
          .set(ContactPhonePage, "+4411223344")
          .success
          .value
          .set(HaveSecondContactPage, true)
          .success
          .value
          .set(SecondContactEmailPage, "test1@email.com")
          .success
          .value
          .set(SecondContactHavePhonePage, true)
          .success
          .value
          .set(SecondContactPhonePage, "+3311211212")
          .success
          .value

        RequestDetailForUpdate.convertToRequestDetails(responseDetail, userAnswers).value mustBe requestDetails

      }

      "must return None if mandatory fields are missing in userAnswers" in {

        val userAnswers = UserAnswers("id")
          .set(HaveTelephonePage, true)
          .success
          .value
          .set(ContactPhonePage, "+4411223344")
          .success
          .value
          .set(HaveSecondContactPage, false)
          .success
          .value

        RequestDetailForUpdate.convertToRequestDetails(responseDetail, userAnswers) mustBe None

      }

      "must return RequestDetailForUpdate when responseDetails secondContact value None is overridden in UserAnswers" in {

        val updatedResponseDetail: ResponseDetail = responseDetail.copy(secondaryContact = None)

        val expectedRequestDetails: RequestDetailForUpdate = RequestDetailForUpdate(
          IDType = "MDR",
          IDNumber = "IDNumber",
          tradingName = Some("Trading Name"),
          isGBUser = true,
          primaryContact = ContactInformation(IndividualDetails("firstName", Some("middleName"), "lastName"), "test@email.com", Some("+4411223344"), None),
          secondaryContact = Some(ContactInformation(OrganisationDetails("SecContactName"), "test1@email.com", Some("+3311211212"), None))
        )

        val userAnswers = UserAnswers("id")
          .set(ContactEmailPage, "test@email.com")
          .success
          .value
          .set(HaveTelephonePage, true)
          .success
          .value
          .set(ContactPhonePage, "+4411223344")
          .success
          .value
          .set(HaveSecondContactPage, true)
          .success
          .value
          .set(SecondContactNamePage, "SecContactName")
          .success
          .value
          .set(SecondContactEmailPage, "test1@email.com")
          .success
          .value
          .set(SecondContactHavePhonePage, true)
          .success
          .value
          .set(SecondContactPhonePage, "+3311211212")
          .success
          .value

        RequestDetailForUpdate.convertToRequestDetails(updatedResponseDetail, userAnswers).value mustBe expectedRequestDetails

      }

      "must return RequestDetailForUpdate when responseDetails haveContactNumber value false is overridden in UserAnswers" in {

        val updatedResponseDetail: ResponseDetail = responseDetail.copy(secondaryContact = None)

        val expectedRequestDetails: RequestDetailForUpdate = RequestDetailForUpdate(
          IDType = "MDR",
          IDNumber = "IDNumber",
          tradingName = Some("Trading Name"),
          isGBUser = true,
          primaryContact = ContactInformation(IndividualDetails("firstName", Some("middleName"), "lastName"), "test@email.com", None, None),
          secondaryContact = Some(ContactInformation(OrganisationDetails("SecContactName"), "test1@email.com", Some("+3311211212"), None))
        )

        val userAnswers = UserAnswers("id")
          .set(ContactEmailPage, "test@email.com")
          .success
          .value
          .set(HaveTelephonePage, false)
          .success
          .value
          .set(ContactPhonePage, "+4411223344")
          .success
          .value
          .set(HaveSecondContactPage, true)
          .success
          .value
          .set(SecondContactNamePage, "SecContactName")
          .success
          .value
          .set(SecondContactEmailPage, "test1@email.com")
          .success
          .value
          .set(SecondContactHavePhonePage, true)
          .success
          .value
          .set(SecondContactPhonePage, "+3311211212")
          .success
          .value

        RequestDetailForUpdate.convertToRequestDetails(updatedResponseDetail, userAnswers).value mustBe expectedRequestDetails

      }

    }
  }
}
