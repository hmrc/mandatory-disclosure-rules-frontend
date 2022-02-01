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

import controllers.actions._
import models.GenericError
import pages.{GenericErrorPage, InvalidXMLPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.TableRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{InvalidXMLFileView, ThereIsAProblemView}

import javax.inject.Inject

class InvalidXMLFileController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: InvalidXMLFileView,
  errorView: ThereIsAProblemView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData) {
    implicit request =>
      (request.userAnswers.get(GenericErrorPage), request.userAnswers.get(InvalidXMLPage)) match {
        case (Some(errors), Some(fileName)) =>
          val xmlErrors = for {
            error <- errors.sorted
          } yield error
          Ok(view(fileName, generateTable(xmlErrors)))
        case _ =>
          InternalServerError(errorView())
      }

  }

  private def generateTable(error: Seq[GenericError])(implicit messages: Messages): Seq[Seq[TableRow]] =
    error.map {
      er =>
        Seq(
          TableRow(content = Text(er.lineNumber.toString), classes = "govuk-table__cell--numeric", attributes = Map("id" -> s"lineNumber_${er.lineNumber}")),
          TableRow(content = Text(messages(er.message.messageKey, er.message.args: _*)), attributes = Map("id" -> s"errorMessage_${er.lineNumber}"))
        )
    }
}
