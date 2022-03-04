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

import connectors.SubmissionConnector
import controllers.actions._
import handlers.XmlHandler
import models.{ConversationId, MDR402, ValidatedFileData}
import pages.{URLPage, ValidXMLPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpResponse
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
  xmlHandler: XmlHandler,
  val controllerComponents: MessagesControllerComponents,
  view: SendYourFileView,
  errorView: ThereIsAProblemView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      val displayWarning = request.userAnswers
        .get(ValidXMLPage)
        .fold(false)(
          validatedFileData => validatedFileData.messageSpecData.messageTypeIndic.equals(MDR402)
        )
      Ok(view(displayWarning))
  }

  //TODO - below method to change when spinny wheel added
  def onSubmit: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      (request.userAnswers.get(ValidXMLPage), request.userAnswers.get(URLPage)) match {
        case (Some(ValidatedFileData(filename, _)), Some(fileUrl)) =>
          val xml = xmlHandler.load(fileUrl)
          for {
            response <- submissionConnector.submitDocument(filename, request.subscriptionId, xml)
          } yield Redirect(routes.FileReceivedController.onPageLoad(response.json.as[ConversationId]))
        case _ =>
          Future.successful(InternalServerError(errorView()))
      }
  }
}
