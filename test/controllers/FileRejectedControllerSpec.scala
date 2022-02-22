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
import connectors.HandleXMLFileConnector
import models.{FileDetails, FileError, Rejected}
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.TableRow
import views.html.FileRejectedView

import java.time.LocalDateTime
import scala.concurrent.Future

class FileRejectedControllerSpec extends SpecBase {

  private val fileName   = "CornerShop"
  private val error      = "Brimful of Asha on the 45"
  private val lineNumber = "1"

  private val errorRows = Seq(
    Seq(
      TableRow(content = Text(lineNumber), classes = "govuk-table__cell--numeric", attributes = Map("id" -> "lineNumber_1")),
      TableRow(content = Text(error), attributes = Map("id" -> "errorMessage_1"))
    )
  )

  "FileRejected Controller" - {

    val mockHandleXMLFileConnector: HandleXMLFileConnector = mock[HandleXMLFileConnector]

    "must return OK and the correct view for a GET" in {

      val conversationId = "conversationId"
      val userAnswers    = emptyUserAnswers
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[HandleXMLFileConnector].toInstance(mockHandleXMLFileConnector)
        )
        .build()

      when(mockHandleXMLFileConnector.getFileDetails(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(
              FileDetails(
                fileName,
                "messageRefId",
                LocalDateTime.parse("2022-01-01T10:30:00.000"),
                LocalDateTime.parse("2022-01-01T10:30:00.000"),
                Rejected(FileError(error)),
                conversationId
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, routes.FileRejectedController.onPageLoad(conversationId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FileRejectedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileName, errorRows)(request, messages(application)).toString
      }
    }

    "must return Internal server error on failing to read error details from userAnswers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.FileRejectedController.onPageLoad("conversationId").url)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
