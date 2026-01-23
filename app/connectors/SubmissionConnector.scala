/*
 * Copyright 2026 HM Revenue & Customs
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

package connectors

import config.FrontendAppConfig
import models.ConversationId
import models.submissions.SubmissionDetails
import play.api.Logging
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionConnector @Inject() (httpClient: HttpClient, config: FrontendAppConfig) extends Logging {

  val submitUrl = url"${config.mdrUrl}/mandatory-disclosure-rules/submit"

  def submitDocument(submissionDetails: SubmissionDetails)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Option[ConversationId]] =
    httpClient.POST[SubmissionDetails, HttpResponse](submitUrl, submissionDetails) map {
      case response if is2xx(response.status) => Some(response.json.as[ConversationId])
      case errorResponse =>
        logger.warn(s"Failed to submitDocument: revived the status: ${errorResponse.status} and message: ${errorResponse.body}")
        None
    }

}
