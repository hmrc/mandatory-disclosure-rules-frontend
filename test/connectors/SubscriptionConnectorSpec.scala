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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import generators.Generators
import models.subscription.ResponseDetail
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionConnectorSpec extends SpecBase with WireMockHelper with Generators with ScalaCheckPropertyChecks with ScalaFutures {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: SubscriptionConnector = app.injector.instanceOf[SubscriptionConnector]
  val submitUrl                             = "/mandatory-disclosure-rules/subscription/read-subscription"

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

  val responseDetail = Json.parse(responseDetailString).as[ResponseDetail]

  "SubmissionConnector" - {

    "must return a ResponseDetails when readSubscription is successful" in {

      server.stubFor(
        post(urlEqualTo(submitUrl))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(responseDetailString)
          )
      )

      whenReady(connector.readSubscription()) {
        result =>
          result mustBe Some(responseDetail)
      }
    }

    "must return a None when readSubscription  fails with InternalServerError" in {
      server.stubFor(
        post(urlEqualTo(submitUrl))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      whenReady(connector.readSubscription()) {
        result =>
          result mustBe None
      }
    }

  }

}
