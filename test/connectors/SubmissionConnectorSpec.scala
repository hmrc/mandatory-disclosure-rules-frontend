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

package connectors

import models.{ConversationId, MDR401, MessageSpecData, MultipleNewInformation}
import models.submissions.SubmissionDetails
import models.upscan.UploadId
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

class SubmissionConnectorSpec extends Connector {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: SubmissionConnector = app.injector.instanceOf[SubmissionConnector]
  val conversationId: ConversationId      = ConversationId("UUID")
  val submitUrl                           = "/mandatory-disclosure-rules/submit"
  val messageSpec                         = MessageSpecData("x9999", MDR401, 2, "OECD1", MultipleNewInformation)
  val uploadId                            = UploadId("uploadId")
  val fileSize                            = 1000L
  val enrolmentID                         = "enrolmentID"
  val documentUrl                         = "dummyURL"
  val checkSum                            = "1234"
  val fileName                            = "test-file.xml"
  val badFileName                         = "test-bad-file.xml"
  "SubmissionConnector" - {

    "must return a 200 on successful submission of xml" in {

      stubPostResponse(submitUrl, OK, Json.toJson(conversationId).toString())

      whenReady(connector.submitDocument(SubmissionDetails(fileName, uploadId, enrolmentID, fileSize, documentUrl, checkSum, messageSpec))) {
        result =>
          result.value mustBe conversationId
      }
    }

    "must return a 400 when submission of xml fails with BadRequest" in {
      stubPostResponse(submitUrl, BAD_REQUEST)

      whenReady(connector.submitDocument(SubmissionDetails(badFileName, uploadId, enrolmentID, fileSize, documentUrl, checkSum, messageSpec))) {
        result =>
          result mustBe None
      }
    }

    "must return a 500 when submission of xml fails with InternalServer Error" in {
      stubPostResponse(submitUrl, INTERNAL_SERVER_ERROR)

      whenReady(connector.submitDocument(SubmissionDetails(fileName, uploadId, enrolmentID, fileSize, documentUrl, checkSum, messageSpec))) {
        result =>
          result mustBe None
      }
    }

  }

}
