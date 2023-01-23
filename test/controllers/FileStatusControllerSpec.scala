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
import connectors.FileDetailsConnector
import models.ConversationId
import models.fileDetails.{FileDetails, Pending}
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileStatusViewModel
import views.html.FileStatusView

import java.time.LocalDateTime
import scala.concurrent.Future

class FileStatusControllerSpec extends SpecBase {

  private val mockFileConnector = mock[FileDetailsConnector]

  "FileStatus Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileConnector)
        )
        .build()

      val fileDetails = Seq(FileDetails("name.xml", "messageRefId1", LocalDateTime.now(), LocalDateTime.now(), Pending, ConversationId("id")))
      when(mockFileConnector.getAllFileDetails(any(), any())).thenReturn(Future.successful(Some(fileDetails)))

      running(application) {
        val request = FakeRequest(GET, routes.FileStatusController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FileStatusView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(FileStatusViewModel.createStatusTable(fileDetails)(messages(application)))(request, messages(application)).toString
      }
    }

    "must return INTERNAL_SERVER_ERROR on failing to get allFileDetails" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileConnector)
        )
        .build()

      when(mockFileConnector.getAllFileDetails(any(), any())).thenReturn(Future.successful(None))

      running(application) {
        val request = FakeRequest(GET, routes.FileStatusController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
