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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.CheckYourAnswersHelper
import viewmodels.govuk.summarylist._
import views.html.ChangeOrganisationContactDetailsView

import javax.inject.Inject

class ChangeOrganisationContactDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  frontendAppConfig: FrontendAppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ChangeOrganisationContactDetailsView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData.apply andThen requireData) {
    implicit request =>
      val checkUserAnswersHelper = new CheckYourAnswersHelper(request.userAnswers)

      val primaryContactList = SummaryListViewModel(
        rows = checkUserAnswersHelper.buildRow()._1.flatten
      )

      val secondaryContactList = SummaryListViewModel(
        rows = checkUserAnswersHelper.buildRow()._2.flatten
      )

      Ok(view(primaryContactList, secondaryContactList, frontendAppConfig))
  }

}
