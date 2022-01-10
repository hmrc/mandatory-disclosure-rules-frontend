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

package models.subscription

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

class UpdateSubscriptionForMDRRequestSpec extends AnyFreeSpec with Matchers {

  val requestCommon: RequestCommonForUpdate = RequestCommonForUpdate(
    regime = "MDR",
    receiptDate = "2020-09-23T16:12:11Z",
    acknowledgementReference = "AB123c",
    originatingSystem = "MDTP",
    requestParameters = None
  )

  val requestDetails: RequestDetailForUpdate = RequestDetailForUpdate(
    IDType = "SAFE",
    IDNumber = "IDNumber",
    tradingName = Some("Trading Name"),
    isGBUser = true,
    primaryContact = ContactInformation(IndividualDetails("firstName", Some("middleName"), "lastName"), "test@email.com", Some("+4411223344"), None),
    secondaryContact = Some(ContactInformation(OrganisationDetails("orgName"), "test@email.com", Some("+4411223344"), None))
  )

  val updateSubscriptionRequest: UpdateSubscriptionForMDRRequest = UpdateSubscriptionForMDRRequest(UpdateSubscriptionDetails(requestCommon, requestDetails))

  "UpdateSubscriptionForMDRRequest" - {
    "serialise to json" in {

      val expectedJson: JsValue = Json.parse("""
          |{
          |  "updateSubscriptionForMDRRequest": {
          |    "requestCommon": {
          |      "regime": "MDR",
          |      "receiptDate": "2020-09-23T16:12:11Z",
          |      "acknowledgementReference": "AB123c",
          |      "originatingSystem": "MDTP"
          |    },
          |    "requestDetail": {
          |      "IDType": "SAFE",
          |      "IDNumber": "IDNumber",
          |      "tradingName": "Trading Name",
          |      "isGBUser": true,
          |      "primaryContact": [
          |        {
          |          "individual": {
          |             "lastName": "lastName",
          |             "firstName": "firstName",
          |             "middleName": "middleName"
          |         },
          |          "email": "test@email.com",
          |          "phone": "+4411223344"
          |        }
          |      ],
          |      "secondaryContact": [
          |        {
          |          "organisation": {
          |            "organisationName": "orgName"
          |          },
          |          "email": "test@email.com",
          |          "phone": "+4411223344"
          |        }
          |      ]
          |    }
          |  }
          |}
          |""".stripMargin)

      Json.toJson(updateSubscriptionRequest) mustBe expectedJson
    }
  }
}
