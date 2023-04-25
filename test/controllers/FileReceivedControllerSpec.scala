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

import connectors.FileDetailsConnector
import base.SpecBase
import models.{ConversationId, MDR401, MessageSpecData, MultipleCorrectionsDeletions, MultipleNewInformation, NormalMode, UserAnswers, ValidatedFileData}
import models.fileDetails.{Accepted, FileDetails}
import org.mockito.ArgumentMatchers.any
import pages.{ContactEmailPage, SecondContactEmailPage, ValidXMLPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import utils.DateTimeFormatUtil.{dateFormatter, timeFormatter}
import viewmodels.govuk.summarylist._
import views.html.FileReceivedView

import java.time.LocalDateTime
import scala.concurrent.Future

class FileReceivedControllerSpec extends SpecBase {

  val mockFileDetailsConnector: FileDetailsConnector = mock[FileDetailsConnector]

  val messageRefId       = "messageRefId"
  val conversationId     = ConversationId("conversationId")
  val time               = "10:30am"
  val date               = "1 January 2022"
  val firstContactEmail  = "first@email.com"
  val secondContactEmail = "second@email.com"

  val localTimeDate = LocalDateTime.parse("2022-01-01T10:30:00.000")

  val baseUserAnswers = UserAnswers("Id")
    .set(ContactEmailPage, firstContactEmail)
    .success
    .value
    .set(SecondContactEmailPage, secondContactEmail)
    .success
    .value

  def summaryRow(msg: String) = SummaryListViewModel(
    Seq(
      SummaryListRow(
        key = Key(Text("File ID (MessageRefId)")),
        value = ValueViewModel(HtmlContent(messageRefId)),
        actions = None
      ),
      SummaryListRow(
        key = Key(Text("Checks completed")),
        value = ValueViewModel(Text(s"${localTimeDate.format(dateFormatter)} at ${localTimeDate.format(timeFormatter)}"))
      ),
      SummaryListRow(
        key = Key(Text("File information")),
        value = ValueViewModel(Text(msg)),
        actions = None
      )
    )
  ).withoutBorders()
    .withCssClass("govuk-!-margin-bottom-0")

  "FileReceived Controller" - {

    "must return OK and the correct view for a GET with MultipleNewInformation" in {

      val vfd: ValidatedFileData = ValidatedFileData("fileName", MessageSpecData("messageRef", MDR401, 2, MultipleNewInformation))

      val userAnswers = baseUserAnswers
        .set(ValidXMLPage, vfd)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      when(mockFileDetailsConnector.getFileDetails(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(
              FileDetails(
                "name",
                messageRefId,
                localTimeDate,
                localTimeDate,
                Accepted,
                conversationId
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, routes.FileReceivedController.onPageLoad(NormalMode, conversationId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FileReceivedView]

        val list = summaryRow("New information in multiple reports")

        status(result) mustEqual OK
        contentAsString(result) mustBe view(list, firstContactEmail, Some(secondContactEmail))(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET with MultipleCorrectionsDeletions" in {

      val vfd: ValidatedFileData = ValidatedFileData("fileName", MessageSpecData("messageRef", MDR401, 2, MultipleCorrectionsDeletions))

      val userAnswers = baseUserAnswers
        .set(ValidXMLPage, vfd)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[FileDetailsConnector].toInstance(mockFileDetailsConnector)
        )
        .build()

      when(mockFileDetailsConnector.getFileDetails(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(
              FileDetails(
                "name",
                messageRefId,
                localTimeDate,
                localTimeDate,
                Accepted,
                conversationId
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, routes.FileReceivedController.onPageLoad(NormalMode, conversationId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FileReceivedView]

        val list = summaryRow("Corrections or deletions for multiple reports")

        status(result) mustEqual OK
        contentAsString(result) mustBe view(list, firstContactEmail, Some(secondContactEmail))(request, messages(application)).toString
      }
    }




  }
}
