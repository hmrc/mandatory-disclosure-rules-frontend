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

package controllers

import connectors.FileDetailsConnector
import controllers.actions._
import models.{ConversationId, SingleOther}
import pages.UploadIDPage
import play.api.i18n.Lang.logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.ContactEmailHelper.getContactEmails
import viewmodels.FileReceivedViewModel
import viewmodels.govuk.summarylist._
import models.cssClassesType.CssClassesType
import views.html.{FileReceivedView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileReceivedController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  fileDetailsConnector: FileDetailsConnector,
  view: FileReceivedView,
  errorView: ThereIsAProblemView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def load(conversationId: ConversationId): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      fileDetailsConnector.getFileDetails(conversationId) flatMap {
        fileDetails =>
          (getContactEmails, fileDetails) match {
            case (Some(emails), Some(details)) =>
              if (details.reportType.contains(SingleOther)) {
                logger.warn("FileReceivedController: Test data submitted but successful outcome")
                Future.successful(InternalServerError(errorView()))
              } else {
                val detailsList =
                  SummaryListViewModel(FileReceivedViewModel.getSummaryRows(details, details.reportType))
                    .withMargin()
                    .withCssClass(CssClassesType.govukMarginBottom)
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.remove(UploadIDPage))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Ok(view(detailsList, emails.firstContact, emails.secondContact))
              }
            case _ =>
              logger.warn("FileReceivedController: Unable to retrieve XML information from UserAnswers")
              Future.successful(InternalServerError(errorView()))
          }
      }
  }
  def onPageLoadFast(conversationId: ConversationId): Action[AnyContent] = load(conversationId)
  def onPageLoadSlow(conversationId: ConversationId): Action[AnyContent] = load(conversationId)
}
