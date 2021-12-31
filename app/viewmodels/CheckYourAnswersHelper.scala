/*
 * Copyright 2021 HM Revenue & Customs
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
import models.requests.DataRequest
import models.{AffinityType, CheckMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.AnyContent
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class CheckYourAnswersHelper(userAnswers: UserAnswers, affinityType: AffinityType)(implicit request: DataRequest[AnyContent], messages: Messages) {

  def buildRow() =
    (Seq(contactNamePage(), contactEmailPage(), contactPhonePage()),
     Seq(hasSecondContactPage(), secondaryContactNamePage(), secondaryContactEmailPage(), secondaryContactPhonePage())
    )

  def contactNamePage(): Option[SummaryListRow] = userAnswers.get(ContactNamePage) map {
    x =>
      SummaryListRowViewModel(
        key = "checkYourAnswers.name.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"$x").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IndexController.onPageLoad().url)
            .withAttribute(("id", "change-corrections"))
        )
      )
  }

  def contactEmailPage(): Option[SummaryListRow] = userAnswers.get(ContactEmailPage) map {
    x =>
      SummaryListRowViewModel(
        key = "contactEmail.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"$x").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.ContactEmailController.onPageLoad(CheckMode, affinityType).url)
            .withAttribute(("id", "change-corrections"))
        )
      )
  }

  def contactPhonePage(): Option[SummaryListRow] = {
    val summaryView = (value: String) =>
      SummaryListRowViewModel(
        key = "contactPhone.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(value).toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.ContactPhoneController.onPageLoad(CheckMode, affinityType).url)
            .withAttribute(("id", "change-corrections"))
        )
      )

    Some(
      userAnswers.get(ContactPhonePage) match {
        case Some(phone) if !phone.isEmpty => summaryView(phone)
        case _                             => summaryView(messages("no.phone"))
      }
    )
  }

  def hasSecondContactPage(): Option[SummaryListRow] = {
    val summaryView = (yesNo: String) =>
      SummaryListRowViewModel(
        key = "hasSecondContact.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"${messages(yesNo)}").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IndexController.onPageLoad().url)
            .withAttribute(("id", "change-corrections"))
        )
      )
    Some(userAnswers.get(SecondContactPage) match {
      case Some(x) =>
        val yesNo = if (x) "site.no" else "site.yes"
        summaryView(yesNo)
      case None =>
        val yesNo = userAnswers.get(SndContactNamePage) match {
          case Some(_) => "site.yes"
          case _       => "site.no"
        }
        summaryView(yesNo)
    })

  }

  def secondaryContactNamePage(): Option[SummaryListRow] = userAnswers.get(SndContactNamePage) map {
    x =>
      SummaryListRowViewModel(
        key = "checkYourAnswers.name.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"$x").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IndexController.onPageLoad().url)
            .withAttribute(("id", "change-corrections"))
        )
      )
  }

  def secondaryContactEmailPage(): Option[SummaryListRow] = userAnswers.get(SndContactEmailPage) map {
    x =>
      SummaryListRowViewModel(
        key = "checkYourAnswers.name.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"$x").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IndexController.onPageLoad().url)
            .withAttribute(("id", "change-corrections"))
        )
      )
  }

  def secondaryContactPhonePage(): Option[SummaryListRow] = userAnswers.get(SndContactPhonePage) map {
    x =>
      SummaryListRowViewModel(
        key = "checkYourAnswers.name.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(s"$x").toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IndexController.onPageLoad().url)
            .withAttribute(("id", "change-corrections"))
        )
      )
  }

}

object CheckYourAnswersHelper {

  def apply(userAnswers: UserAnswers)(implicit request: DataRequest[AnyContent], messages: Messages) =
    new CheckYourAnswersHelper(userAnswers, AffinityType(request.userType))
}
