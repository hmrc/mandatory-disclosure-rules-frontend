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

import models.FileDetails
import utils.DateTimeFormatUtil
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}

object FileStatusViewModel {

  def createStatusTable(allFiles: Seq[FileDetails])(implicit messages: Messages): Table = {

    val tableRow: Seq[Seq[TableRow]] = allFiles map (
      file =>
        Seq(
          TableRow(Text(file.name), classes = "app-custom-class govuk-!-width-one-half"),
          TableRow(Text(DateTimeFormatUtil.dateFormatted(file.submitted)), classes = "app-custom-class govuk-!-width-one-half"),
          TableRow(Text(file.status.toString), classes = "app-custom-class govuk-!-width-one-half")
        )
    )

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
