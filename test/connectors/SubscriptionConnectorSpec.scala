/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors

import generators.ModelGenerators
import models.subscription.{RequestDetailForUpdate, ResponseDetail}
import org.scalacheck.Arbitrary
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionConnectorSpec extends Connector with ModelGenerators {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: SubscriptionConnector = app.injector.instanceOf[SubscriptionConnector]
  private val readSubscriptionUrl           = "/mandatory-disclosure-rules/subscription/read-subscription"
  private val updateSubscriptionUrl         = "/mandatory-disclosure-rules/subscription/update-subscription"

  val responseDetailString: String =
    """
      |{
      |"subscriptionID": "111111111",
      |"tradingName": "",
      |"isGBUser": true,
      |"primaryContact":
      |{
      |"email": "",
      |"phone": "",
      |"mobile": "",
      |"individual": {
      |"lastName": "Last",
      |"firstName": "First"
      |}
      |},
      |"secondaryContact":
      |{
      |"email": "",
      |"organisation": {
      |"organisationName": ""
      |}
      |}
      |}""".stripMargin

  val responseDetail: ResponseDetail = Json.parse(responseDetailString).as[ResponseDetail]

  "SubmissionConnector" - {
    "readSubscription" - {
      "must return a ResponseDetails when readSubscription is successful" in {
        stubPostResponse(readSubscriptionUrl, OK, responseDetailString)

        whenReady(connector.readSubscription()) {
          result =>
            result mustBe Some(responseDetail)
        }
      }

      "must return a None when readSubscription  fails with InternalServerError" in {
        stubPostResponse(readSubscriptionUrl, INTERNAL_SERVER_ERROR)

        whenReady(connector.readSubscription()) {
          result =>
            result mustBe None
        }
      }
    }

    "updateSubscription" - {
      "must return status 200 when updateSubscription is successful" in {
        val requestDetails = Arbitrary.arbitrary[RequestDetailForUpdate].sample.value
        stubPostResponse(updateSubscriptionUrl, OK)

        whenReady(connector.updateSubscription(requestDetails)) {
          result =>
            result mustBe true
        }
      }

      "must return a error status code when updateSubscription fails with Error" in {
        val requestDetails = Arbitrary.arbitrary[RequestDetailForUpdate].sample.value

        val errorCode = errorCodes.sample.value
        stubPostResponse(updateSubscriptionUrl, errorCode)

        whenReady(connector.updateSubscription(requestDetails)) {
          result =>
            result mustBe false
        }
      }
    }
  }

}
