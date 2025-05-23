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

import controllers.actions._
import pages.{GenericErrorPage, InvalidXMLPage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.ErrorViewHelper
import views.html.{InvalidXMLFileView, ThereIsAProblemView}

import javax.inject.Inject

class InvalidXMLFileController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: InvalidXMLFileView,
  errorViewHelper: ErrorViewHelper,
  errorView: ThereIsAProblemView
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      (request.userAnswers.get(GenericErrorPage), request.userAnswers.get(InvalidXMLPage)) match {
        case (Some(errors), Some(fileName)) =>
          val xmlErrors = for {
            error <- errors.sorted
          } yield error
          Ok(view(fileName, errorViewHelper.generateTable(xmlErrors)))
        case _ =>
          logger.warn("InvalidXMLFileController: Unable to retrieve either Invalid XML information or GenericErrors from UserAnswers")
          InternalServerError(errorView())
      }
  }
}
