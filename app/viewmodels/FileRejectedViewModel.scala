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

  private def docIdContent(docRefIds: Seq[String]): Html = Html(
    docRefIds
      .map(
        docRefId => s"<div class='govuk-!-padding-bottom-2 text-overflow'>$docRefId</div>"
      )
      .mkString(" ")
  )

  //noinspection ScalaStyle
  private def handleCustomErrors(errorDetails: Option[String], docRefIDInError: Option[Seq[String]])(implicit messages: Messages) =
    errorDetails match {
      case Some(`error_details_901`) => ("901", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.901.value"))
      case Some(`error_details_902`) => ("902", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.902.value"))
      case Some(`error_details_903`) => ("903", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.903.value"))
      case Some(`error_details_904`) => ("904", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.904.value"))
      case Some(`error_details_905`) => ("905", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.905.value"))
      case Some(`error_details_906`) => ("906", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.906.value"))
      case Some(`error_details_907`) => ("907", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.907.value"))
      case Some(`error_details_908`) => ("908", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.908.value"))
      case Some(`error_details_909`) => ("909", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.909.value"))
      case Some(`error_details_910`) => ("910", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.910.value"))
      case Some(`error_details_911`) => ("911", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.911.value"))
      case Some(`error_details_912`) => ("912", HtmlContent(docIdContent(docRefIDInError.getOrElse(Nil))), Messages(s"fileRejected.912.value"))
      case _                         => throw new Exception(s"The received RecordError details: ${errorDetails.getOrElse("")} is not the expected error details")
    }

  private def createTableRow(validationErrors: ValidationErrors)(implicit messages: Messages): Seq[Seq[TableRow]] = {
    val fileErrors: Option[Seq[(String, Content, String)]] = validationErrors.fileError.map(
      _.map(
        error => (Messages(s"fileRejected.${error.code.code}.key"), Text(Messages("label.file")), Messages(s"fileRejected.${error.code.code}.value"))
      )
    )

    val recordErrors: Option[Seq[(String, Content, String)]] = validationErrors.recordError.map(
      _.map(
        recordError =>
          recordError.code match {
            case RecordErrorCode.CustomError => handleCustomErrors(recordError.details, recordError.docRefIDInError)
            case errorCode =>
              (Messages(s"fileRejected.${errorCode.code}.key"),
               HtmlContent(docIdContent(recordError.docRefIDInError.getOrElse(Nil))),
               Messages(s"fileRejected.${errorCode.code}.value")
              )
          }
      )
    )

    val errors: Seq[(String, Content, String)] = (fileErrors ++ recordErrors).flatten.toSeq

    errors.map {
      case (code, docRefId, details) =>
        Seq(
          TableRow(code, attributes = Map("id" -> s"code_$code")),
          TableRow(docRefId, attributes = Map("id" -> s"docRefId_$code")),
          TableRow(details, attributes = Map("id" -> s"errorMessage_$code"))
        )
    }
  }

  val error_details_901 =
    "The CorrDocRefId does not match a DocRefId from the same type of section (either Disclosing or MdrReport). It must refer to the same element"
  val error_details_902 = "The MdrReport CorrDocRefId does not refer to the same previously sent MdrBody as the Disclosing element"
  val error_details_903 = "The Disclosing section contains resent data (DocTypeIndic = OECD0) so it must not have a CorrDocRefId"
  val error_details_904 = "Disclosing Capacity is not one of the allowed values for the MdrReport CrsAvoidance or OOS Reason provided"
  val error_details_905 = "Since the DocTypeIndic of Disclosing is OECD0, the DocTypeIndic of MdrReport must be OECD2"

  val error_details_906 =
    "Since the MdrReport has a DocTypeIndic of OECD3, indicating this section must be deleted, this Disclosing section must be deleted too"

  val error_details_907 =
    "Since the MessageTypeIndic contains the value of MDR401 for new information, the Disclosing DocTypeIndic must contain the value of OECD1 for new information"

  val error_details_908 =
    "Since the MessageTypeIndic contains the value of MDR401 for new information, an MdrReport section must be provided with a DocTypeIndic of OECD1 for new information"
  val error_details_909 = "DocRefId must be 100 characters or less, start with your 15-character MDR ID and include up to 85 other characters of your choice"
  val error_details_910 = "MessageRefId must be 85 characters or less, start with your 15-character MDR ID and include up to 70 other characters of your choice"
  val error_details_911 = "Provide an issuedBy for every TIN that has a value other than NOTIN"
  val error_details_912 = "The top level of the StructureChart must not have an Ownership or InvestAmount"

  val errorList: Seq[String] = Seq(
    error_details_901,
    error_details_902,
    error_details_903,
    error_details_904,
    error_details_905,
    error_details_906,
    error_details_907,
    error_details_908,
    error_details_909,
    error_details_910,
    error_details_911,
    error_details_912
  )
}
