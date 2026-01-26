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

package connectors

import config.FrontendAppConfig
import models.ConversationId
import models.fileDetails.{FileDetails, FileStatus}
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, StringContextOps}
import play.api.libs.json.*
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URI
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileDetailsConnector @Inject() (httpClient: HttpClientV2, config: FrontendAppConfig) extends Logging {

  def getAllFileDetails(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Seq[FileDetails]]] = {
    val url = s"${config.mdrUrl}/mandatory-disclosure-rules/files/details"

    httpClient.get(new URI(url).toURL).execute[HttpResponse].map {
      case responseMessage if is2xx(responseMessage.status) =>
        responseMessage.json.asOpt[Seq[FileDetails]]
      case _ =>
        logger.warn("FileDetailsConnector: Failed to get AllFileDetails")
        None
    }
  }

  def getFileDetails(conversationId: ConversationId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[FileDetails]] = {
    val url = url"${config.mdrUrl}/mandatory-disclosure-rules/files/${conversationId.value}/details"
    httpClient.get(url).execute[HttpResponse].map {
      case responseMessage if is2xx(responseMessage.status) =>
        responseMessage.json
          .asOpt[FileDetails]
      case _ =>
        logger.warn("FileDetailsConnector: Failed to get FileDetails")
        None
    }
  }

  def getStatus(conversationId: ConversationId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[FileStatus]] = {
    val url = url"${config.mdrUrl}/mandatory-disclosure-rules/files/${conversationId.value}/status"
    httpClient.get(url).execute[HttpResponse].map { httpResponse =>
      httpResponse.json.asOpt[FileStatus]
    }
  }
}
