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

package controllers

import base.SpecBase
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubscriptionService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class ChangeIndividualContactDetailsControllerSpec extends SpecBase {

  val mockSubscriptionService: SubscriptionService = mock[SubscriptionService]

  override def beforeEach: Unit = {
    reset(mockSubscriptionService)
    super.beforeEach
  }

  "ChangeIndividualContactDetails Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ChangeIndividualContactDetailsController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual OK
      }
    }

    "redirect to confirmation page on updating ContactDetails" in { // TODO replace with actual confirmation page
      when(mockSubscriptionService.updateContactDetails(any[UserAnswers]())(any[HeaderCarrier]()))
        .thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SubscriptionService].toInstance(mockSubscriptionService)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.ChangeIndividualContactDetailsController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual NOT_IMPLEMENTED
      }
    }

    "load 'technical difficulties' page on failing to update ContactDetails" in {
      when(mockSubscriptionService.updateContactDetails(any[UserAnswers]())(any[HeaderCarrier]()))
        .thenReturn(Future.successful(false))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SubscriptionService].toInstance(mockSubscriptionService)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.ChangeIndividualContactDetailsController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
