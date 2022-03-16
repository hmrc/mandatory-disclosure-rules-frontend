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
import connectors.FileDetailsConnector
import models.fileDetails.{FileDetails, Pending}
import models.{ConversationId, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.{ConversationIdPage, FileDetailsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileStatusViewModel
import views.html.FilePendingChecksView

import java.time.LocalDateTime
import scala.concurrent.Future

class FilePendingChecksControllerSpec extends SpecBase {

  "FilePendingChecks Controller" - {

    val mockFileDetailsConnector: FileDetailsConnector = mock[FileDetailsConnector]

    "must return OK and the correct view for a GET when fileStatus is Pending" in {

      val fileDetails = FileDetails(
        "name",
        "messageRefId",
        LocalDateTime.parse("2022-01-01T10:30:00.000"),
        LocalDateTime.parse("2022-01-01T10:30:00.000"),
        Pending,
        ConversationId("conversationId")
      )

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(ConversationIdPage, ConversationId("conversationId"))
        .success
        .value
        .set(FileDetailsPage, fileDetails)
        .success
        .value

      when(mockFileDetailsConnector.getFileDetails(any())(any(), any())).thenReturn(Future.successful(Some(fileDetails)))

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      val fileSummaryList = FileStatusViewModel.createFileSummary("name", Pending)(messages(application))
      val action          = routes.FilePendingChecksController.onPageLoad().url

      running(application) {

        val request = FakeRequest(GET, routes.FilePendingChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[FilePendingChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action)(request, messages(application)).toString
      }
    }
  }
}
