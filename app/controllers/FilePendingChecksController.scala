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

package controllers

import connectors.FileDetailsConnector
import controllers.actions._
import models.fileDetails.{Pending, Rejected, Accepted => FileStatusAccepted}
import pages.{ConversationIdPage, ValidXMLPage}
import play.api.i18n.Lang.logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.FileStatusViewModel
import views.html.{FilePendingChecksView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FilePendingChecksController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  fileConnector: FileDetailsConnector,
  val controllerComponents: MessagesControllerComponents,
  view: FilePendingChecksView,
  errorView: ThereIsAProblemView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      (request.userAnswers.get(ValidXMLPage), request.userAnswers.get(ConversationIdPage)) match {
        case (Some(xmlDetails), Some(conversationId)) =>
          fileConnector.getStatus(conversationId) flatMap {
            case Some(FileStatusAccepted) =>
              Future.successful(Redirect(routes.FilePassedChecksController.onPageLoad()))
            case Some(Rejected(_)) =>
              Future.successful(Redirect(routes.FileRejectedController.onPageLoad(conversationId))) //TODO - change to FileFailedChecks Page
            case Some(Pending) =>
              val summary = FileStatusViewModel.createFileSummary(xmlDetails.fileName, "Pending")
              Future.successful(Ok(view(summary, routes.FilePendingChecksController.onPageLoad().url)))
            case _ =>
              logger.error("Unable to get Status")
              Future.successful(InternalServerError(errorView()))
          }
        case _ =>
          logger.error("Unable to retrieve fileName & conversationId")
          Future.successful(InternalServerError(errorView()))
      }
  }
}
