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

package controllers

import base.{SpecBase, TestValues}
import connectors.FileDetailsConnector
import models.fileDetails.{Accepted, FileDetails}
import models.{ConversationId, SingleNewInformation, UserAnswers}
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import services.SubscriptionService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.IndexView

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class IndexControllerSpec (implicit val ec: ExecutionContext) extends SpecBase {

  "Index Controller" - {

    "must return OK and the correct view for a GET" - {

      "when there are recent file upload details" in {
        val mockSubscriptionService = mock[SubscriptionService]
        val mockFileConnector       = mock[FileDetailsConnector]
        val mockSessionRepository   = mock[SessionRepository]

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[SubscriptionService].toInstance(mockSubscriptionService),
            bind[FileDetailsConnector].toInstance(mockFileConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

        val userAnswers = UserAnswers(TestValues.id)
        when(mockSubscriptionService.getContactDetails(any[UserAnswers]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Some(userAnswers)))
        when(mockSessionRepository.set(any[UserAnswers]())).thenReturn(Future.successful(true))
        when(mockFileConnector.getAllFileDetails(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(
            Future.successful(
              Some(
                Seq(
                  FileDetails(TestValues.fileName,
                              TestValues.messageRefId,
                              Some(SingleNewInformation),
                              LocalDateTime.now(),
                              LocalDateTime.now(),
                              Accepted,
                              TestValues.conversationId
                  )
                )
              )
            )
          )

        running(application) {
          val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[IndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual view(
            TestValues.subscriptionId,
            routes.UploadFileController.onPageLoad().url,
            controllers.routes.ChangeOrganisationContactDetailsController.onPageLoad().url,
            showRecentFiles = true
          )(
            request,
            messages(application)
          ).toString
        }
      }

      "when there are no recent file upload details" in {
        val mockSubscriptionService = mock[SubscriptionService]
        val mockFileConnector       = mock[FileDetailsConnector]
        val mockSessionRepository   = mock[SessionRepository]

        val application = applicationBuilder(userAnswers = None)
          .overrides(
            bind[SubscriptionService].toInstance(mockSubscriptionService),
            bind[FileDetailsConnector].toInstance(mockFileConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

        val userAnswers = UserAnswers(TestValues.id)
        when(mockSubscriptionService.getContactDetails(any[UserAnswers]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Some(userAnswers)))
        when(mockSessionRepository.set(any[UserAnswers]())).thenReturn(Future.successful(true))
        when(mockFileConnector.getAllFileDetails(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(Future.successful(None))

        running(application) {
          val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[IndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual view(
            TestValues.subscriptionId,
            routes.UploadFileController.onPageLoad().url,
            controllers.routes.ChangeOrganisationContactDetailsController.onPageLoad().url,
            showRecentFiles = false
          )(
            request,
            messages(application)
          ).toString
        }
      }
    }
    "must redirect to error page when an error occurs" in {

      val mockSubscriptionService = mock[SubscriptionService]
      val mockSessionRepository   = mock[SessionRepository]
      val mockFileConnector       = mock[FileDetailsConnector]

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[FileDetailsConnector].toInstance(mockFileConnector),
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      when(mockSubscriptionService.getContactDetails(any[UserAnswers]())(any[HeaderCarrier]()))
        .thenReturn(Future.successful(None))
      when(mockSessionRepository.set(any[UserAnswers]())).thenReturn(Future.successful(true))
      when(mockFileConnector.getAllFileDetails(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(Future.successful(None))

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }
  }
}
