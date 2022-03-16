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
import pages.{ConversationIdPage, FileDetailsPage}
import play.api.i18n.Lang.logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
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
  sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  view: FilePendingChecksView,
  errorView: ThereIsAProblemView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      request.userAnswers.get(ConversationIdPage) match {
        case Some(conversationId) =>
          fileConnector.getFileDetails(conversationId) flatMap {
            case Some(fileDetails) =>
              for {
                userAnswers <- Future.fromTry(request.userAnswers.set(FileDetailsPage, fileDetails))
                _           <- sessionRepository.set(userAnswers)
              } yield fileDetails.status match {
                case FileStatusAccepted =>
                  Redirect(routes.FilePassedChecksController.onPageLoad())
                case Rejected(_) =>
                  Redirect(routes.FileRejectedController.onPageLoad(fileDetails.conversationId)) // TODO - change to FileFailedChecks Page
                case Pending =>
                  val summary = FileStatusViewModel.createFileSummary(fileDetails.name, fileDetails.status)
                  Ok(view(summary, routes.FilePendingChecksController.onPageLoad().url))
              }
            case _ =>
              logger.error("Unable to retrieve fileDetails")
              Future.successful(InternalServerError(errorView()))
          }
        case _ =>
          logger.error("ConversationId missing")
          Future.successful(InternalServerError(errorView()))
      }
  }
}
