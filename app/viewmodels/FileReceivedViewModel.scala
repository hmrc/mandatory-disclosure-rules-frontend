/*
 * Copyright 2026 HM Revenue & Customs
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

import models.fileDetails.FileDetails
import models.{MultipleCorrectionsDeletions, MultipleNewInformation, ReportType, SingleCorrection, SingleNewInformation}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.DateTimeFormatUtil.{dateFormatter, timeFormatter}
import viewmodels.govuk.summarylist._

object FileReceivedViewModel {

  def getSummaryRows(details: FileDetails, reportType: Option[ReportType])(implicit messages: Messages): Seq[SummaryListRow] = {

    val time = details.submitted.format(timeFormatter).toLowerCase
    val date = details.submitted.format(dateFormatter)
    val reportTypeRow: Option[SummaryListRow] = reportType.map {
      report =>
        SummaryListRowViewModel(
          key = "fileReceived.messageTypeIndic",
          value = ValueViewModel(HtmlFormat.escape(s"${displayTypeIndicator(report)}").toString),
          actions = Seq()
        )
    }

    Seq(
      SummaryListRowViewModel(
        key = "fileReceived.messageRefId.key",
        value = ValueViewModel(HtmlFormat.escape(s"${details.messageRefId}").toString),
        actions = Seq()
      ),
      SummaryListRowViewModel(
        key = "fileReceived.ChecksCompleted.key",
        value = ValueViewModel(messages("fileReceived.ChecksCompleted.time", date, time))
      )
    ) ++ reportTypeRow
  }

  private def displayTypeIndicator(typeIndic: ReportType)(implicit messages: Messages) =
    typeIndic match {
      case MultipleNewInformation       => messages("fileReceived.messageTypeIndic.MultipleNewInformation")
      case MultipleCorrectionsDeletions => messages("fileReceived.messageTypeIndic.MultipleCorrectionsDeletions")
      case SingleNewInformation         => messages("fileReceived.messageTypeIndic.SingleNewInformation") // oecd1
      case SingleCorrection             => messages("fileReceived.messageTypeIndic.SingleCorrection") // oecd0 || oecd2
      case _                            => messages("fileReceived.messageTypeIndic.SingleDeletion") // oecc3
    }

}
