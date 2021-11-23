/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.{UpscanConnector, ValidationConnector}
import generators.Generators
import models.{Dac6MetaData, UserAnswers}
import models.upscan._
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.UploadIDPage
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class UploadFileControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val fakeUpscanConnector     = app.injector.instanceOf[FakeUpscanConnector]
  val mockSessionRepository   = mock[SessionRepository]
  val mockValidationConnector = mock[ValidationConnector]

  val userAnswers = UserAnswers(userAnswersId)
    .set(UploadIDPage, UploadId("uploadId"))
    .success
    .value

  val application = applicationBuilder(userAnswers = Some(userAnswers))
    .overrides(
      bind[UpscanConnector].toInstance(fakeUpscanConnector)
    )
    .build()

  "upload form controller" - {

    "must initiate a request to upscan to bring back an upload form" in {
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val request        = FakeRequest(GET, routes.UploadFileController.onPageLoad().url)
      val result         = route(application, request).value

      status(result) mustBe OK
      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())
      templateCaptor.getValue mustEqual "upload-form.njk"
    }

    "must read the progress of the upload from the backend" in {

      val request = FakeRequest(GET, routes.UploadFileController.getStatus().url)

      def verifyResult(uploadStatus: UploadStatus, expectedResult: Int = SEE_OTHER, expectedUI: String = ""): Unit = {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UpscanConnector].toInstance(fakeUpscanConnector)
          )
          .build()

        fakeUpscanConnector.setStatus(uploadStatus)

        when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))

        val templateCaptor = ArgumentCaptor.forClass(classOf[String])

        val result = route(application, request).value

        status(result) mustBe expectedResult
        if (expectedResult == OK) {
          verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())
          templateCaptor.getValue mustEqual expectedUI
        }

        application.stop()
        reset(mockRenderer)
      }

      verifyResult(InProgress, OK, "upload-result.njk")
      verifyResult(Quarantined)
      verifyResult(UploadRejected(ErrorDetails("REJECTED", "message")), OK, "upload-form.njk")
      verifyResult(Failed, INTERNAL_SERVER_ERROR, "serviceError.njk")
      verifyResult(UploadedSuccessfully("name", "downloadUrl"))

    }

    "must show any returned error" in {

      val controller = application.injector.instanceOf[UploadFileController]

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
      when(mockAppConfig.sendEmailToggle).thenReturn(true)

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val argumentCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = controller.showError("errorCode", "errorMessage", "errorReqId")(FakeRequest("", ""))

      status(result) mustBe INTERNAL_SERVER_ERROR
      verify(mockRenderer, times(1)).render(templateCaptor.capture(), argumentCaptor.capture())(any())
      templateCaptor.getValue mustEqual "serviceError.njk"
    }

    "must show File to large error when the errorCode is EntityTooLarge" in {

      val controller = application.injector.instanceOf[UploadFileController]

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
      when(mockAppConfig.sendEmailToggle).thenReturn(true)

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val argumentCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = controller.showError("EntityTooLarge", "Your proposed upload exceeds the maximum allowed size", "errorReqId")(FakeRequest("", ""))

      status(result) mustBe OK
      verify(mockRenderer, times(1)).render(templateCaptor.capture(), argumentCaptor.capture())(any())
      templateCaptor.getValue mustEqual "fileTooLargeError.njk"
      val captured = argumentCaptor.getValue
      (captured \\ "xmlTechnicalGuidanceUrl").head
        .as[String] mustEqual "https://www.gov.uk/government/publications/cross-border-tax-arrangements-schema-and-supporting-documents"
    }

    "must display result page while file is successfully updated " in {

      val controller = application.injector.instanceOf[UploadFileController]

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val result         = controller.showResult()(FakeRequest("", ""))

      status(result) mustBe OK
      verify(mockRenderer, times(1)).render(templateCaptor.capture(), any())(any())
      templateCaptor.getValue mustEqual "upload-result.njk"

    }

  }

}
