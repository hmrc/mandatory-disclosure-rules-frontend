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
import connectors.{UpscanConnector, ValidationConnector}
import helpers.FakeUpscanConnector
import models.upscan.{Reference, UploadId, UploadSessionDetails, UploadedSuccessfully}
import models.{GenericError, InvalidXmlError, Message, UserAnswers, ValidatedFileData, ValidationErrors}
import org.bson.types.ObjectId
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.UploadIDPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FilenameLengthyView

import scala.concurrent.{ExecutionContextExecutor, Future}

class FileValidationControllerSpec extends SpecBase with BeforeAndAfterEach {

  val fileSize                                     = 1000L
  val mockValidationConnector: ValidationConnector = mock[ValidationConnector]

  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  override def beforeEach(): Unit =
    reset(mockSessionRepository)

  val fakeUpscanConnector: FakeUpscanConnector = app.injector.instanceOf[FakeUpscanConnector]

  "FileValidationController" - {
    val uploadId    = UploadId("123")
    val userAnswers = UserAnswers(userAnswersId).set(UploadIDPage, uploadId).success.value
    val application = applicationBuilder(userAnswers = Some(userAnswers))
      .overrides(
        bind[UpscanConnector].toInstance(fakeUpscanConnector),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[ValidationConnector].toInstance(mockValidationConnector)
      )
      .build()

    val downloadURL = "http://dummy-url.com"
    val uploadDetails = UploadSessionDetails(
      new ObjectId(),
      UploadId("123"),
      Reference("123"),
      UploadedSuccessfully("afile", downloadURL, fileSize, "1234")
    )

    "must redirect to Check your answers and present the correct view for a GET" in {

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      val expectedData: JsObject =
        Json.obj("uploadID" -> UploadId("123"), "validXML" -> ValidatedFileData("afile", TestValues.messageSpecData, fileSize, "1234"), "url" -> downloadURL)

      when(mockValidationConnector.sendForValidation(any())(any(), any())).thenReturn(Future.successful(Right(TestValues.messageSpecData)))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
      fakeUpscanConnector.setDetails(uploadDetails)

      val request                = FakeRequest(GET, routes.FileValidationController.onPageLoad().url)
      val result: Future[Result] = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.CheckYourFileDetailsController.onPageLoad().url

      verify(mockSessionRepository, times(1)).set(userAnswersCaptor.capture())
      userAnswersCaptor.getValue.data mustEqual expectedData
    }

    "must redirect to invalid XML page if XML validation fails" in {

      val errors: Seq[GenericError]                      = Seq(GenericError(1, Message("error")))
      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      val expectedData                                   = Json.obj("invalidXML" -> "afile", "errors" -> errors)

      fakeUpscanConnector.setDetails(uploadDetails)

      when(mockValidationConnector.sendForValidation(any())(any(), any())).thenReturn(Future.successful(Left(ValidationErrors(errors, None))))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val controller             = application.injector.instanceOf[FileValidationController]
      val result: Future[Result] = controller.onPageLoad()(FakeRequest("", ""))

      status(result) mustBe SEE_OTHER
      verify(mockSessionRepository, times(1)).set(userAnswersCaptor.capture())

      userAnswersCaptor.getValue.data mustEqual expectedData
    }

    "must redirect to file error page if XML parser fails" in {

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      val expectedData                                   = Json.obj("invalidXML" -> "afile")

      fakeUpscanConnector.setDetails(uploadDetails)
      // noinspection ScalaStyle

      when(mockValidationConnector.sendForValidation(any())(any(), any())).thenReturn(Future.successful(Left(InvalidXmlError("sax exception"))))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val controller             = application.injector.instanceOf[FileValidationController]
      val result: Future[Result] = controller.onPageLoad()(FakeRequest("", ""))

      status(result) mustBe SEE_OTHER
      verify(mockSessionRepository, times(1)).set(userAnswersCaptor.capture())
      redirectLocation(result) mustBe Some(routes.FileErrorController.onPageLoad().url)
      userAnswersCaptor.getValue.data mustEqual expectedData
    }

    "must return an INTERNAL_SERVER_ERROR when a valid UploadId cannot be found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[UpscanConnector].toInstance(fakeUpscanConnector),
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[ValidationConnector].toInstance(mockValidationConnector)
        )
        .build()

      val controller             = application.injector.instanceOf[FileValidationController]
      val result: Future[Result] = controller.onPageLoad()(FakeRequest("", ""))

      status(result) mustBe INTERNAL_SERVER_ERROR
    }

    "must return an INTERNAL_SERVER_ERROR when meta data cannot be found" in {

      fakeUpscanConnector.resetDetails()

      val controller             = application.injector.instanceOf[FileValidationController]
      val result: Future[Result] = controller.onPageLoad()(FakeRequest("", ""))

      status(result) mustBe INTERNAL_SERVER_ERROR
    }

    "must redirect to filename lengthy page if filename is too long" in {

      val givenFilename =
        """sample-mdr-file-individual-very_very-very_very-sample-mdr-file-individual-very_very-very_very-sample-mdr-file-individual-
          |very_very-very_very-sample-mdr-file-individual-very_very-very_very""".stripMargin

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      val expectedData: JsObject                         = Json.obj("filenameLengthy" -> givenFilename)

      when(mockValidationConnector.sendForValidation(any())(any(), any())).thenReturn(Future.successful(Right(TestValues.messageSpecData)))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
      val givenUploadDetails = UploadSessionDetails(
        new ObjectId(),
        UploadId("123"),
        Reference("123"),
        UploadedSuccessfully(givenFilename, downloadURL, fileSize, "1234")
      )
      fakeUpscanConnector.setDetails(givenUploadDetails)

      val request                = FakeRequest(GET, routes.FileValidationController.onPageLoad().url)
      val result: Future[Result] = route(application, request).value

      val view = application.injector.instanceOf[FilenameLengthyView]

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.FilenameLengthyController.onPageLoad().url

      verify(mockSessionRepository, times(1)).set(userAnswersCaptor.capture())
      userAnswersCaptor.getValue.data mustEqual expectedData

      view(givenFilename)(request, messages(application)).toString must include("the filename is longer than 100 characters")
    }
  }
}
