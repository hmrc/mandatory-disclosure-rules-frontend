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

import config.FrontendAppConfig
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class TestProcessEISResponseConnector @Inject() (httpClient: HttpClient, config: FrontendAppConfig) extends Logging {

  val submitUrl = s"${config.mdrUrl}/mandatory-disclosure-rules/validation-result"

  def submitEISResponse(conversationId: String, xmlDocument: NodeSeq)(ec: ExecutionContext): Future[HttpResponse] = {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val headers = Seq(
      HeaderNames.CONTENT_TYPE  -> "application/xml",
      "x-conversation-id"       -> conversationId
    )

    httpClient.POSTString[HttpResponse](submitUrl, xmlDocument.toString(), headers)
  }
}
