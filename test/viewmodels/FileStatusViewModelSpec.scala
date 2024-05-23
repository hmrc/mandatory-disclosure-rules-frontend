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

package viewmodels

import base.SpecBase
import models.{ConversationId, SingleNewInformation}
import models.fileDetails.FileErrorCode.{fileErrorCodesForProblemStatus, CustomError => FileCustomError}
import models.fileDetails.RecordErrorCode.CustomError
import models.fileDetails._
import uk.gov.hmrc.govukfrontend.views.Aliases.{TableRow, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table}

import java.time.LocalDateTime
import scala.util.Random

class FileStatusViewModelSpec extends SpecBase {

  "FileStatusViewModel" - {
    val header: Option[List[HeadCell]] = Some(
      List(
        HeadCell(Text("File"), None, "app-custom-class mdr-width-file", None, None, Map()),
        HeadCell(Text("Uploaded"), None, "app-custom-class mdr-width-uploaded", None, None, Map()),
        HeadCell(Text("Result"), None, "app-custom-class mdr-width-result", None, None, Map()),
        HeadCell(Text("Next step"), None, "app-custom-class mdr-width-next", None, None, Map())
      )
    )

    "must return the viewModel to display on the page for the valid input" in {
      val expectedTable = Table(
        List(
          List(
            TableRow(Text("name3.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("18 Mar 2022 11:10am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--red'>Failed</strong>"), None, "", None, None, Map()),
            TableRow(
              HtmlContent("<a href='/report-under-mandatory-disclosure-rules/report/problem/business-rule-errors/id' class='govuk-link'>Check errors</a>"),
              None,
              "app-custom-class mdr-width-next",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("name2.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("18 Mar 2022 10:15am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--green'>Passed</strong>"), None, "", None, None, Map()),
            TableRow(
              HtmlContent("<a href='/report-under-mandatory-disclosure-rules/report/file-confirmation/id' class='govuk-link'>Go to confirmation</a>"),
              None,
              "app-custom-class mdr-width-next",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("name1.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("18 Mar 2022 8:10am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--yellow'>Pending</strong>"), None, "", None, None, Map()),
            TableRow(HtmlContent("<p class='govuk-visually-hidden'>None</p>"), None, "app-custom-class mdr-width-next", None, None, Map())
          )
        ),
        header,
        Some("Result of automatic checks"),
        "govuk-table__caption govuk-visually-hidden",
        firstCellIsHeader = true,
        "",
        Map()
      )

      val rejected = Rejected(ValidationErrors(None, None))

      val fileDetails =
        Seq(
          FileDetails(
            "name1.xml",
            "messageRefId1",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-18T08:10:00.324"),
            LocalDateTime.now(),
            Pending,
            ConversationId("id")
          ),
          FileDetails(
            "name2.xml",
            "messageRefId2",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-18T10:15:00.324"),
            LocalDateTime.now(),
            Accepted,
            ConversationId("id")
          ),
          FileDetails(
            "name3.xml",
            "messageRefId3",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-18T11:10:00.324"),
            LocalDateTime.now(),
            rejected,
            ConversationId("id")
          )
        )

      FileStatusViewModel.createStatusTable(fileDetails)(messages(app)) mustBe expectedTable

    }

    "must return the viewModel with all the file status to display on the page for the valid input" in {
      val expectedTable = Table(
        List(
          List(
            TableRow(Text("name1.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("19 Mar 2022 11:16am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--yellow'>Pending</strong>"), None, "", None, None, Map()),
            TableRow(HtmlContent("<p class='govuk-visually-hidden'>None</p>"), None, "app-custom-class mdr-width-next", None, None, Map())
          ),
          List(
            TableRow(Text("name2.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("18 Mar 2022 11:09am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--purple'>Problem</strong>"), None, "", None, None, Map()),
            TableRow(
              HtmlContent("<a href='/report-under-mandatory-disclosure-rules/report/problem/file-not-accepted' class='govuk-link'>Contact us</a>"),
              None,
              "app-custom-class mdr-width-next",
              None,
              None,
              Map()
            )
          )
        ),
        header,
        Some("Result of automatic checks"),
        "govuk-table__caption govuk-visually-hidden",
        firstCellIsHeader = true,
        "",
        Map()
      )

      val errorCode = Random.shuffle(fileErrorCodesForProblemStatus).head

      val rejected = Rejected(ValidationErrors(Some(Seq(FileErrors(errorCode, None))), None))

      val fileDetails =
        Seq(
          FileDetails(
            "name1.xml",
            "messageRefId1",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-19T11:16:19.324"),
            LocalDateTime.now(),
            Pending,
            ConversationId("id")
          ),
          FileDetails(
            "name2.xml",
            "messageRefId4",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-18T11:09:10.324"),
            LocalDateTime.now(),
            rejected,
            ConversationId("id")
          )
        )

      FileStatusViewModel.createStatusTable(fileDetails)(messages(app)) mustBe expectedTable
    }

    "must return the viewModel with the file status 'Problem'" in {
      val expectedTable = Table(
        List(
          List(
            TableRow(Text("name1.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("19 Mar 2022 11:16am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--yellow'>Pending</strong>"), None, "", None, None, Map()),
            TableRow(HtmlContent("<p class='govuk-visually-hidden'>None</p>"), None, "app-custom-class mdr-width-next", None, None, Map())
          ),
          List(
            TableRow(Text("name2.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("18 Mar 2022 11:09am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--purple'>Problem</strong>"), None, "", None, None, Map()),
            TableRow(
              HtmlContent("<a href='/report-under-mandatory-disclosure-rules/report/problem/file-not-accepted' class='govuk-link'>Contact us</a>"),
              None,
              "app-custom-class mdr-width-next",
              None,
              None,
              Map()
            )
          ),
          List(
            TableRow(Text("name3.xml"), None, "mdr-table-filename", None, None, Map()),
            TableRow(Text("17 Mar 2022 11:09am"), None, "", None, None, Map()),
            TableRow(HtmlContent("<strong class='govuk-tag govuk-tag--purple'>Problem</strong>"), None, "", None, None, Map()),
            TableRow(
              HtmlContent("<a href='/report-under-mandatory-disclosure-rules/report/problem/file-not-accepted' class='govuk-link'>Contact us</a>"),
              None,
              "app-custom-class mdr-width-next",
              None,
              None,
              Map()
            )
          )
        ),
        header,
        Some("Result of automatic checks"),
        "govuk-table__caption govuk-visually-hidden",
        firstCellIsHeader = true,
        "",
        Map()
      )

      val rejected: Rejected     = Rejected(ValidationErrors(None, Some(Seq(RecordError(CustomError, Some("random error details"), None)))))
      val fileRejected: Rejected = Rejected(ValidationErrors(Some(Seq(FileErrors(FileCustomError, None))), None))

      val fileDetails =
        Seq(
          FileDetails(
            "name1.xml",
            "messageRefId1",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-19T11:16:19.324"),
            LocalDateTime.now(),
            Pending,
            ConversationId("id1")
          ),
          FileDetails(
            "name2.xml",
            "messageRefId4",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-18T11:09:10.324"),
            LocalDateTime.now(),
            rejected,
            ConversationId("id2")
          ),
          FileDetails(
            "name3.xml",
            "messageRefId4",
            Some(SingleNewInformation),
            LocalDateTime.parse("2022-03-17T11:09:10.324"),
            LocalDateTime.now(),
            fileRejected,
            ConversationId("id3")
          )
        )

      FileStatusViewModel.createStatusTable(fileDetails)(messages(app)) mustBe expectedTable
    }

  }
}
