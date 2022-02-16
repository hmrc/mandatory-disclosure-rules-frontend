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

import models.{Accepted, FileDetails, FileError, Pending, Rejected}
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class HandleXMLFileConnectorSpec extends Connector {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules-stub.port" -> server.port()
    )
    .build()

  lazy val connector: HandleXMLFileConnector = app.injector.instanceOf[HandleXMLFileConnector]

  private val conversationId = "conversationId3"

  private val allFilesUrls = s"/mdr/all-files/$mdrId"
  private val fileUrl      = s"/mdr/file/$conversationId"

  private val allFiles: String = """
      |[
      |  {
      |    "name": "test1.xml",
      |    "submitted": "2022-02-10T15:35:37.636",
      |    "status": {
      |      "_type": "models.Pending"
      |    },
      |    "conversationId": "conversationId1"
      |  },
      |  {
      |    "name": "test2.xml",
      |    "submitted": "2022-02-10T15:35:37.636",
      |    "status": {
      |      "error": {
      |        "detail": "error"
      |      },
      |      "_type": "models.Rejected"
      |    },
      |    "conversationId": "conversationId2"
      |  }
      |]""".stripMargin

  private val file: String = """
     |  {
     |    "name": "test3.xml",
     |    "submitted": "2022-02-10T15:35:37.636",
     |    "status": {
     |      "_type": "models.Accepted"
     |    },
     |    "conversationId": "conversationId3"
     |  }""".stripMargin

  "HandleXMLFileConnector" - {

    "getAllFileDetails" - {

      "must return 'all file details' when getAllFileDetails is successful" in {
        val expectedResult = Some(
          Seq(
            FileDetails("test1.xml", LocalDateTime.parse("2022-02-10T15:35:37.636"), Pending, "conversationId1"),
            FileDetails("test2.xml", LocalDateTime.parse("2022-02-10T15:35:37.636"), Rejected(FileError("error")), "conversationId2")
          )
        )

        stubGetResponse(allFilesUrls, OK, allFiles)

        val result = connector.getAllFileDetails(mdrId)

        result.futureValue mustBe expectedResult
      }

      "must return 'None' when getAllFileDetails is successful but response json is invalid" in {

        stubGetResponse(allFilesUrls, OK)

        val result = connector.getAllFileDetails(mdrId)

        result.futureValue mustBe None
      }

      "must return 'None' when getAllFileDetails fails with Error" in {

        val errorCode = errorCodes.sample.value
        stubGetResponse(allFilesUrls, errorCode)

        val result = connector.getAllFileDetails(mdrId)

        result.futureValue mustBe None

      }
    }

    "getFileDetails" - {

      "must return 'file details' when getFileDetails is successful" in {
        val expectedResult = Some(
          FileDetails("test3.xml", LocalDateTime.parse("2022-02-10T15:35:37.636"), Accepted, "conversationId3")
        )

        stubGetResponse(fileUrl, OK, file)

        val result = connector.getFileDetails(conversationId)

        result.futureValue mustBe expectedResult
      }

      "must return 'None' when getFileDetails is successful but response json is invalid" in {

        stubPostResponse(fileUrl, OK)

        val result = connector.getFileDetails(mdrId)

        result.futureValue mustBe None
      }

      "must return 'None' when getFileDetails fails with Error" in {

        val errorCode = errorCodes.sample.value
        stubPostResponse(fileUrl, errorCode)

        val result = connector.getFileDetails(conversationId)

        result.futureValue mustBe None

      }
    }
  }

}
