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

import connectors.{FileDetailsConnector, SubmissionConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import handlers.XmlHandler
import models.fileDetails.{Accepted => FileStatusAccepted, Pending, Rejected}
import models.upscan.RedirectAsJson
import models.{MDR402, ValidatedFileData}
import pages.{ConversationIdPage, URLPage, ValidXMLPage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{SendYourFileView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SendYourFileController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  submissionConnector: SubmissionConnector,
  fileDetailsConnector: FileDetailsConnector,
  sessionRepository: SessionRepository,
  xmlHandler: XmlHandler,
  val controllerComponents: MessagesControllerComponents,
  view: SendYourFileView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      val displayWarning = request.userAnswers
        .get(ValidXMLPage)
        .fold(false)(
          validatedFileData => validatedFileData.messageSpecData.messageTypeIndic.equals(MDR402)
        )
      Future.successful(Ok(view(displayWarning)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      (request.userAnswers.get(ValidXMLPage), request.userAnswers.get(URLPage)) match {
        case (Some(ValidatedFileData(filename, _)), Some(fileUrl)) =>
          val xml = xmlHandler.load(fileUrl)
          submissionConnector.submitDocument(filename, request.subscriptionId, xml) flatMap {
            case Some(conversationId) =>
              for {
                userAnswers <- Future.fromTry(request.userAnswers.set(ConversationIdPage, conversationId))
                _           <- sessionRepository.set(userAnswers)
              } yield Ok
            case _ => Future.successful(InternalServerError)
          }
        case _ =>
          Future.successful(InternalServerError)
      }
  }

  def getStatus: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      request.userAnswers.get(ConversationIdPage) match {
        case Some(conversationId) =>
          logger.info(s"The conversation id:$conversationId")
          fileDetailsConnector.getStatus(conversationId) flatMap {
            case Some(FileStatusAccepted) =>
              Future.successful(Ok(Json.toJson(RedirectAsJson(routes.FileReceivedController.onPageLoad(conversationId).url))))
            case Some(Rejected(_)) =>
              Future.successful(Ok(Json.toJson(RedirectAsJson(routes.FileRejectedController.onPageLoad(conversationId).url))))
            case Some(Pending) =>
              Future.successful(NoContent)
            case None =>
              logger.info("getStatus: no status returned")
              Future.successful(InternalServerError)
          }
        case None =>
          logger.info("UserAnswers.ConversationId is empty")
          Future.successful(InternalServerError)
      }
  }
}
