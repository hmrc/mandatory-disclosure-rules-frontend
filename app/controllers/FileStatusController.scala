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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.FileStatusViewModel
import views.html.{FileStatusView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class FileStatusController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  fileConnector: FileDetailsConnector,
  val controllerComponents: MessagesControllerComponents,
  view: FileStatusView,
  errorView: ThereIsAProblemView
)(implicit
  executionContext: ExecutionContext
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      fileConnector.getAllFileDetails map {
        case Some(allFiles) => Ok(view(FileStatusViewModel.createStatusTable(allFiles)))
        case _              => InternalServerError(errorView())
      }
  }
}
