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

package services

import base.SpecBase
import connectors.SubscriptionConnector
import models.subscription.ResponseDetail
import org.mockito.ArgumentMatchers.any
import pages.{ContactEmailPage, ContactNamePage, ContactPhonePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionServiceSpec extends SpecBase {

  val mockSubscriptionConnector: SubscriptionConnector = mock[SubscriptionConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .overrides(
      bind[SubscriptionConnector].toInstance(mockSubscriptionConnector)
    )

  val service: SubscriptionService = app.injector.instanceOf[SubscriptionService]

  "SubscriptionService" - {
    "GetContactDetails" - {
      "must call the subscription connector and return a UserAnswers populated with returned contact details for Organisation" in {
        val responseDetailString: String =
          """
            |{
            |"subscriptionID": "111111111",
            |"tradingName": "",
            |"isGBUser": true,
            |"primaryContact": [
            |{
            |"email": "test@test.com",
            |"phone": "99999",
            |"mobile": "",
            |"organisation": {
            |"organisationName": "acme"
            |}
            |}
            |],
            |"secondaryContact": [
            |{
            |"email": "test@test.com",
            |"phone": "99999",
            |"mobile": "",
            |"organisation": {
            |"organisationName": "wer"
            |}
            |}
            |]
            |}""".stripMargin

        val responseDetail = Json.parse(responseDetailString).as[ResponseDetail]

        when(mockSubscriptionConnector.readSubscription()(any[HeaderCarrier](), any[ExecutionContext]())).thenReturn(Future.successful(Some(responseDetail)))

        val result = service.getContactDetails(emptyUserAnswers)

        val ua = result.futureValue.right.get

        ua.get(ContactNamePage) mustBe Some("acme")
        ua.get(ContactEmailPage) mustBe Some("test@test.com")
        ua.get(ContactPhonePage) mustBe Some("99999")
      }

      "must call the subscription connector and return a UserAnswers populated with returned contact details for Individual" in {
        val responseDetailString: String =
          """
            |{
            |"subscriptionID": "111111111",
            |"tradingName": "",
            |"isGBUser": true,
            |"primaryContact": [
            |{
            |"email": "test@test.com",
            |"phone": "99999",
            |"mobile": "",
            |"individual": {
            |"firstName" : "fname",
            |"lastName" : "lname"
            |}
            |}
            |]
            |}""".stripMargin

        val responseDetail = Json.parse(responseDetailString).as[ResponseDetail]

        when(mockSubscriptionConnector.readSubscription()(any[HeaderCarrier](), any[ExecutionContext]())).thenReturn(Future.successful(Some(responseDetail)))

        val result = service.getContactDetails(emptyUserAnswers)

        val ua = result.futureValue.right.get

        ua.get(ContactEmailPage) mustBe Some("test@test.com")
        ua.get(ContactPhonePage) mustBe Some("99999")
      }
    }
  }
}
