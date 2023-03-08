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

import controllers.actions._
import models.CheckMode
import pages.{ConversationIdPage, ValidXMLPage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.FileCheckViewModel
import views.html.{FileFailedChecksView, ThereIsAProblemView}

import javax.inject.Inject

class FileFailedChecksController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: FileFailedChecksView,
  errorView: ThereIsAProblemView
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      (request.userAnswers.get(ValidXMLPage), request.userAnswers.get(ConversationIdPage)) match {
        case (Some(xmlDetails), Some(conversationId)) =>
          val action  = routes.FileRejectedController.onPageLoad(CheckMode, conversationId).url
          val summary = FileCheckViewModel.createFileSummary(xmlDetails.fileName, "Rejected")
          Ok(view(summary, action))
        case _ =>
          logger.warn("FileFailedChecksController: Unable to retrieve either XML information or ConversationId from UserAnswers")
          InternalServerError(errorView())
      }
  }
}
