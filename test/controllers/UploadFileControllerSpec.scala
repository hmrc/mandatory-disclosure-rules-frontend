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

package controllers

import base.SpecBase
import connectors.UpscanConnector
import forms.UploadFileFormProvider
import generators.Generators
import helpers.FakeUpscanConnector
import models.UserAnswers
import models.upscan._
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.UploadIDPage
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.Json
import java.time.Instant
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import views.html.UploadFileView

import scala.concurrent.Future

class UploadFileControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val fileSize                                 = 1000L
  val uploadId: UploadId                       = UploadId("12345")
  val fakeUpscanConnector: FakeUpscanConnector = app.injector.instanceOf[FakeUpscanConnector]

  val userAnswers: UserAnswers = UserAnswers(userAnswersId)
    .set(UploadIDPage, UploadId("uploadId"))
    .success
    .value

  val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
    .overrides(
      bind[UpscanConnector].toInstance(fakeUpscanConnector)
    )
    .build()

  "upload file controller" - {

    "must initiate a request to upscan to bring back an upload form" in {
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val form    = app.injector.instanceOf[UploadFileFormProvider]
      val request = FakeRequest(GET, routes.UploadFileController.onPageLoad().url)
      val result  = route(application, request).value

      val view = application.injector.instanceOf[UploadFileView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form(), UpscanInitiateResponse(Reference(""), "target", Map.empty))(request, messages(application)).toString
    }

    "must read the progress of the upload from the backend" in {

      val request = FakeRequest(GET, routes.UploadFileController.getStatus(uploadId).url)

      def verifyResult(uploadStatus: UploadStatus, expectedResult: Option[String]): Unit = {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UpscanConnector].toInstance(fakeUpscanConnector)
          )
          .build()

        fakeUpscanConnector.setStatus(uploadStatus)
        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe expectedResult
        application.stop()
      }

      verifyResult(InProgress, Some(routes.UploadFileController.getStatus(uploadId).url))
      verifyResult(Quarantined, Some(routes.VirusFileFoundController.onPageLoad().url))
      verifyResult(UploadRejected(ErrorDetails("REJECTED", "message")), Some(routes.NotXMLFileController.onPageLoad().url))
      verifyResult(Failed, Some(routes.ThereIsAProblemController.onPageLoad().url))
      verifyResult(UploadedSuccessfully("name", "downloadUrl", fileSize, "1234"), Some(routes.FileValidationController.onPageLoad().url))

    }

    "must show any returned error" in {

      val request = FakeRequest(GET, routes.UploadFileController.showError("errorCode", "errorMessage", "errorReqId").url)
      val result  = route(application, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must show File to large error when the errorCode is EntityTooLarge" in {

      val request =
        FakeRequest(GET, routes.UploadFileController.showError("EntityTooLarge", "Your proposed upload exceeds the maximum allowed size", "errorReqId").url)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.FileTooLargeController.onPageLoad().url)
    }

    "must show errorForm when rejected upload has 'octet-stream' in message" in {
      val rejectedDetails = UploadDetails(
        Instant.now(),
        "1234",
        "application/octet-stream",
        "file-name" // Simulate a rejected octet-stream
      )
      val rejected = UploadRejected(ErrorDetails("REJECTED", "message"))

      fakeUpscanConnector.setStatus(rejected)

      val request = FakeRequest(GET, routes.UploadFileController.getStatus(uploadId).url)
      val result  = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.NotXMLFileController.onPageLoad().url)
    }

    "must redirect to ThereIsAProblemController on other rejection errors" in {
      val otherRejected = UploadRejected(ErrorDetails("REJECTED", "other-error-message"))

      fakeUpscanConnector.setStatus(otherRejected)

      val request = FakeRequest(GET, routes.UploadFileController.getStatus(uploadId).url)
      val result  = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.NotXMLFileController.onPageLoad().url)
    }

  }
}
