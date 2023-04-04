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

package controllers.testOnlyDoNotUseInAppConf

import com.google.inject.Inject
import connectors.SubmissionConnector
import controllers.actions.IdentifierAction
import models.submissions.SubmissionDetails
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext
import scala.xml.NodeSeq

class TestSubmissionController @Inject() (
  identifierAction: IdentifierAction,
  connector: SubmissionConnector,
  override val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with Logging {

  /** ***********************************************
    * Will take a URL pointing to a file served from stubs
    * *************************************************
    */

  def insertTestSubmission(fileName: String) = identifierAction(parse.json).async {
    implicit request =>
      //ToDo Get url from request body
      val url = request.body.as[Url]
      logger.debug(s"inserting test submission: ${request.body}")
      connector
        .submitDocument(SubmissionDetails(fileName, request.subscriptionId, None, url.url))
        .map {
          case Some(conversationId) => Ok(Json.toJson(conversationId))
          case _                    => InternalServerError
        }
  }

}
