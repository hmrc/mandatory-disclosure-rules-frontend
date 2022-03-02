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

package connectors

import config.FrontendAppConfig
import models.ConversationId
import models.fileDetails.FileDetails
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HandleXMLFileConnector @Inject() (httpClient: HttpClient, config: FrontendAppConfig) extends Logging {

  def getAllFileDetails(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Seq[FileDetails]]] = {
    val url = s"${config.mdrUrl}/mandatory-disclosure-rules/files/details"
    httpClient.GET[HttpResponse](url).map {
      case responseMessage if is2xx(responseMessage.status) =>
        responseMessage.json
          .asOpt[Seq[FileDetails]]
      case _ =>
        logger.warn("HandleXMLFileConnector: Failed to get AllFileDetails")
        None
    }
  }

  def getFileDetails(conversationId: ConversationId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[FileDetails]] = {
    val url = s"${config.mdrUrl}/mandatory-disclosure-rules/files/${conversationId.value}/details"
    httpClient.GET(url).map {
      case responseMessage if is2xx(responseMessage.status) =>
        responseMessage.json
          .asOpt[FileDetails]
      case _ =>
        logger.warn("HandleXMLFileConnector: Failed to get FileDetails")
        None
    }
  }
}
