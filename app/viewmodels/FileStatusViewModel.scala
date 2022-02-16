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

package viewmodels

import controllers.routes
import models.{Accepted, FileDetails, FileStatus, Pending, Rejected}
import models.FileDetails._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import utils.DateTimeFormatUtil

object FileStatusViewModel {

  private def htmlStatus(fileStatus: FileStatus)(implicit messages: Messages): Content = {
    val cssClass = Messages(s"cssColour.${fileStatus.toString}")
    val status   = Messages(s"status.${fileStatus.toString}")

    HtmlContent(s"<strong class='govuk-tag govuk-tag--$cssClass'>$status</strong>")
  }

  private def buildTableRow(fileStatus: FileStatus, conversationId: String)(implicit messages: Messages): TableRow = {
    val action = fileStatus match {
      case Pending     => ""
      case Accepted    => s"<a href='${routes.FileReceivedController.onPageLoad(conversationId).url}'>${Messages("fileStatus.accepted")}</a>"
      case Rejected(_) => s"<a href=''>${Messages("fileStatus.rejected")}</a>"
    }

    TableRow(HtmlContent(action), classes = "app-custom-class govuk-!-width-one-half")
  }

  def createStatusTable(allFileDetails: Seq[FileDetails])(implicit messages: Messages): Table = {

    val tableRow: Seq[Seq[TableRow]] = allFileDetails.sortBy(_.submitted) map {
      fileDetails =>
        Seq(
          TableRow(Text(fileDetails.name)),
          TableRow(Text(DateTimeFormatUtil.dateFormatted(fileDetails.submitted))),
          TableRow(htmlStatus(fileDetails.status)),
          buildTableRow(fileDetails.status, fileDetails.conversationId)
        )
    }

    val header = Some(
      Seq(
        HeadCell(Text(Messages("fileStatus.file")), classes = "app-custom-class govuk-!-width-one-half"),
        HeadCell(Text(Messages("fileStatus.sent")), classes = "app-custom-class govuk-!-width-one-half"),
        HeadCell(Text(Messages("fileStatus.fileStatus")), classes = "app-custom-class"),
        HeadCell(Text(Messages("fileStatus.nextSteps")), classes = "app-custom-class")
      )
    )

    Table(rows = tableRow, head = header, caption = Some(Messages("fileStatus.fileStatus")))
  }

}
