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

import models.fileDetails._
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}

object FileRejectedViewModel {

  def createTable(validationErrors: ValidationErrors)(implicit messages: Messages): Table = {

    val header = Some(
      Seq(
        HeadCell(Text(Messages("fileRejected.code"))),
        HeadCell(Text(Messages("fileRejected.docRefId")), classes = "govuk-!-width-one-half"),
        HeadCell(Text(Messages("fileRejected.error")), classes = "govuk-!-width-one-half")
      )
    )
    Table(
      rows = createTableRow(validationErrors),
      head = header,
      firstCellIsHeader = false,
      caption = Some(Messages("fileRejected.tableCaption")),
      captionClasses = "govuk-table__caption govuk-heading-m"
    )
  }

  def handleCustomErrors(errorDetails: Option[String]) = {
    errorDetails match {
      case Some()
    }


  }

  private def createTableRow(validationErrors: ValidationErrors)(implicit messages: Messages): Seq[Seq[TableRow]] = {
    val fileErrors: Option[Seq[(String, Content, String)]] = validationErrors.fileError.map(
      _.map(
        error => (Messages(s"fileRejected.${error.code.code}.key"), Text(Messages("label.file")), Messages(s"fileRejected.${error.code.code}.value"))
      )
    )
    def docIdContent(docRefIds: Seq[String]): Html = Html(
      docRefIds
        .map(
          docRefId => s"<div class='govuk-!-padding-bottom-2 text-overflow'>$docRefId</div>"
        )
        .mkString(" ")
    )

    val recordErrors: Option[Seq[(String, Content, String)]] = validationErrors.recordError.map(
      _.map(
        recordError =>
          recordError.code match {
            case RecordErrorCode.CustomError => handleCustomErrors(recordError.details)
            case errorCode => (Messages(s"fileRejected.${errorCode.code}.key"),
              HtmlContent(docIdContent(recordError.docRefIDInError.getOrElse(Nil))),
              Messages(s"fileRejected.${errorCode.code}.value")
            )
          }
      )
    )

    val errors: Seq[(String, Content, String)] = (fileErrors ++ recordErrors).flatten.toSeq

    errors.map {
      case (code, docRefId, details) => Seq(TableRow(code), TableRow(docRefId), TableRow(details))
    }
  }
}
