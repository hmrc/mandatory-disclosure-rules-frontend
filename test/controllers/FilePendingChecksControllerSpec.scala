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

import base.{SpecBase, TestValues}
import connectors.FileDetailsConnector
import models.fileDetails.FileErrorCode.{FailedSchemaValidation, MessageRefIDHasAlreadyBeenUsed}
import models.fileDetails.RecordErrorCode.{DocRefIDFormat, MissingCorrDocRefId}
import models.fileDetails.{Pending, Rejected, ValidationErrors, Accepted => FileStatusAccepted, _}
import models.{ConversationId, MDR401, MessageSpecData, MultipleNewInformation, UserAnswers, ValidatedFileData}
import org.mockito.ArgumentMatchers.any
import pages.{ConversationIdPage, ValidXMLPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileCheckViewModel
import views.html.{FilePendingChecksView, ThereIsAProblemView}

import scala.concurrent.Future

class FilePendingChecksControllerSpec extends SpecBase {

  val fileSize = 1000L

  "FilePendingChecks Controller" - {

    val mockFileDetailsConnector: FileDetailsConnector = mock[FileDetailsConnector]

    "must return OK and the correct view for a GET when fileStatus is Pending" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(Pending)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      val fileSummaryList = FileCheckViewModel.createFileSummary(TestValues.validatedFileData.fileName, "Pending")(messages(application))
      val action          = routes.FilePendingChecksController.onPageLoad().url

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[FilePendingChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action, TestValues.conversationId.value)(request, messages(application)).toString
      }
    }

    "must redirect to ThereIsAProblem page when file status is invalid" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(None))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[ThereIsAProblemView]

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }

    "must redirect to File Problem Page when REJECTED status returned with 'problem' errors" in {

      val validationErrors = ValidationErrors(Some(Seq(FileErrors(FailedSchemaValidation, None))), Some(Seq(RecordError(DocRefIDFormat, None, None))))

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(Rejected(validationErrors))))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.FileProblemController.onPageLoad().url
      }
    }

    "must redirect to File Problem Page when REJECTED status returned with regular errors" in {

      val validationErrors =
        ValidationErrors(Some(Seq(FileErrors(MessageRefIDHasAlreadyBeenUsed, None))), Some(Seq(RecordError(MissingCorrDocRefId, None, None))))

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(Rejected(validationErrors))))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.FileFailedChecksController.onPageLoad().url
      }
    }

    "must redirect to virus Page when RejectedSDESVirus status returned with regular errors" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(RejectedSDESVirus)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.VirusFileFoundController.onPageLoad().url
      }
    }

    "must redirect to problem Page when RejectedSDES status returned with regular errors" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(RejectedSDES)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.FileProblemController.onPageLoad().url
      }
    }

    "must redirect to File Passed Checks Page when ACCEPTED status returned" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value

      when(mockFileDetailsConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(Some(FileStatusAccepted)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.FilePassedChecksController.onPageLoad().url
      }
    }

    "must throw an internal server error when no file data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[ThereIsAProblemView]

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
