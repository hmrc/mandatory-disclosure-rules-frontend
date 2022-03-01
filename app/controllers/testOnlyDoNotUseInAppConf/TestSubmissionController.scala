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

package controllers.testOnlyDoNotUseInAppConf

import com.google.inject.Inject
import connectors.SubmissionConnector
import controllers.actions.IdentifierAction
import play.api.Logging
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext
import scala.xml.{Elem, NodeSeq}

class TestSubmissionController @Inject() (
  identifierAction: IdentifierAction,
  connector: SubmissionConnector,
  override val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with Logging {

  def insertTestSubmission(fileName: String): Action[NodeSeq] = identifierAction(parse.xml).async {
    implicit request =>
      val xml = request.body.asInstanceOf[Elem]
      logger.info(s"inserting test submission: $xml")
      connector
        .submitDocument(fileName, request.subscriptionId, xml)
        .map(
          response =>
            if (is2xx(response.status)) {
              Ok(response.body)
            } else {
              Status(response.status)
            }
        )
  }

}
