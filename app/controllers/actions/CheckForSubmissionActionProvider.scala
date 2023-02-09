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

package controllers.actions

import controllers.routes
import models.requests.DataRequest
import pages.{JourneyInProgressPage, UploadIDPage}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckForSubmissionActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends CheckForSubmissionAction {

  override def apply(checkFileSubmission: Boolean): ActionRefiner[DataRequest, DataRequest] =
    new CheckForSubmissionActionProvider(checkFileSubmission)

}

class CheckForSubmissionActionProvider @Inject() (checkFileSubmission: Boolean)(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[DataRequest, DataRequest] {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    if (
      (checkFileSubmission && request.userAnswers.get(UploadIDPage).isEmpty) ||
      (!checkFileSubmission && request.userAnswers.get(JourneyInProgressPage).isEmpty)
    ) {
      Future.successful(Left(Redirect(routes.InformationSentController.onPageLoad())))
    } else {
      Future.successful(Right(request))
    }
}

trait CheckForSubmissionAction {
  def apply(checkFileSubmission: Boolean = false): ActionRefiner[DataRequest, DataRequest]
}
