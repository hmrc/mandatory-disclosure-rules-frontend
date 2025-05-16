/*
 * Copyright 2025 HM Revenue & Customs
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

import viewmodels.govuk.summarylist._
import controllers.routes
import models.{
  MultipleCorrectionsDeletions,
  MultipleNewInformation,
  ReportType,
  SingleCorrection,
  SingleDeletion,
  SingleNewInformation,
  SingleOther,
  ValidatedFileData
}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object CheckYourFileDetailsViewModel {

  def getSummaryRows(vfd: ValidatedFileData)(implicit messages: Messages): Seq[SummaryListRow] =
    Seq(
      SummaryListRowViewModel(
        key = "checkYourFileDetails.uploadedFile",
        value = ValueViewModel(HtmlFormat.escape(s"${vfd.fileName}").toString),
        actions = Seq()
      ),
      SummaryListRowViewModel(
        key = "checkYourFileDetails.messageRefId",
        value = ValueViewModel(HtmlFormat.escape(s"${vfd.messageSpecData.messageRefId}").toString),
        actions = Seq()
      ),
      SummaryListRowViewModel(
        key = "checkYourFileDetails.messageTypeIndic",
        value = ValueViewModel(HtmlFormat.escape(s"${displayTypeIndicator(vfd.messageSpecData.reportType)}").toString),
        actions = Seq(
          ActionItemViewModel("checkYourFileDetails.changeFile", routes.UploadFileController.onPageLoad().url)
            .withAttribute(("id", "your-file"))
        )
      )
    )

  private def displayTypeIndicator(typeIndic: ReportType)(implicit messages: Messages) =
    typeIndic match {
      case MultipleNewInformation       => messages("checkYourFileDetails.messageTypeIndic.MultipleNewInformation")
      case MultipleCorrectionsDeletions => messages("checkYourFileDetails.messageTypeIndic.MultipleCorrectionsDeletions")
      case SingleNewInformation         => messages("checkYourFileDetails.messageTypeIndic.SingleNewInformation")
      case SingleCorrection             => messages("checkYourFileDetails.messageTypeIndic.SingleCorrection")
      case SingleDeletion               => messages("checkYourFileDetails.messageTypeIndic.SingleDeletion")
      case SingleOther                  => messages("checkYourFileDetails.messageTypeIndic.SingleOther")
    }

}
