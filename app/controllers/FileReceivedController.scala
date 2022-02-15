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

import connectors.HandleXMLFileConnector
import controllers.actions._
import pages.MessageSpecDataPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.language.LanguageUtils
import utils.ContactEmailHelper.getContactEmails
import views.html.{FileReceivedView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileReceivedController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: FileReceivedView,
  errorView: ThereIsAProblemView,
  handleXMLFileConnector: HandleXMLFileConnector,
  languageUtils: LanguageUtils
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      handleXMLFileConnector.getFileDetails("conversationId3") flatMap {
        fileDetails =>
          fileDetails.fold {
            logger.error("Cannot find file details")
            Future.successful(InternalServerError(errorView()))
          } {
            details =>
              request.userAnswers
                .get(MessageSpecDataPage)
                .fold {
                  logger.error("Cannot find message spec data")
                  Future.successful(InternalServerError(errorView()))
                } {
                  messageSpecData =>
                    val messageRefId = messageSpecData.messageRefId
                    val time         = s"${details.submitted.getHour}:${details.submitted.getMinute}"
                    val date         = languageUtils.Dates.formatDate(details.submitted.toLocalDate)
                    getContactEmails.fold {
                      Future.successful(InternalServerError(errorView()))
                    } {
                      emails =>
                        Future.successful(Ok(view(messageRefId, time, date, emails.firstContactEmail, emails.secondContactEmail)))
                    }
                }
          }
      }
  }
}
