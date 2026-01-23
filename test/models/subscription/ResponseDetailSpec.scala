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

package models.subscription

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class ResponseDetailSpec extends AnyFreeSpec with Matchers {

  "ResponseDetail" - {
    "must de-serialise to model" in {

      val expectedResponseDetails: ResponseDetail = ResponseDetail(
        "yu789428932",
        Some("Tools for Traders"),
        isGBUser = true,
        ContactInformation(IndividualDetails("Tim", None, "Taylor"), "Tim@toolsfortraders.com", Some("078803423883"), Some("078803423883")),
        Some(ContactInformation(OrganisationDetails("Tools for Traders Limited"), "contact@toolsfortraders.com", None, None))
      )

      val json: String =
        """
          |   {
          |   "subscriptionID": "yu789428932",
          |   "tradingName": "Tools for Traders",
          |   "isGBUser": true,
          |   "primaryContact":
          |    {
          |     "email": "Tim@toolsfortraders.com",
          |     "phone": "078803423883",
          |     "mobile": "078803423883",
          |     "individual": {
          |      "lastName": "Taylor",
          |      "firstName": "Tim"
          |     }
          |    },
          |   "secondaryContact":
          |    {
          |     "email": "contact@toolsfortraders.com",
          |     "organisation": {
          |      "organisationName": "Tools for Traders Limited"
          |     }
          |    }
          |    }
          |""".stripMargin

      Json.parse(json).as[ResponseDetail] mustBe expectedResponseDetails

    }

  }
}
