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

import models.fileDetails.FileErrorCode.MessageRefIDHasAlreadyBeenUsed
import models.fileDetails.RecordErrorCode.MessageTypeIndic
import models.fileDetails._
import models.{fileDetails, ConversationId, SingleNewInformation}
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class FileDetailsConnectorSpec extends Connector {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: FileDetailsConnector = app.injector.instanceOf[FileDetailsConnector]

  private val conversationId1 = ConversationId("conversationId1")
  private val conversationId2 = ConversationId("conversationId2")
  private val conversationId3 = ConversationId("conversationId3")
  private val testXml1        = "test1.xml"
  private val messageRefId1   = "messageRefId1"

  private val testXml2      = "test2.xml"
  private val messageRefId2 = "messageRefId2"

  private val testXml3      = "test3.xml"
  private val messageRefId3 = "messageRefId3"

  private val dateString1   = "2022-02-10T15:35:37.636"
  private val dateString2   = "2022-02-10T15:45:37.636"
  private val allFilesUrls  = "/mandatory-disclosure-rules/files/details"
  private val fileUrl       = s"/mandatory-disclosure-rules/files/${conversationId3.value}/details"
  private val fileStatusUrl = s"/mandatory-disclosure-rules/files/${conversationId3.value}/status"

  private val allFiles: String = """
      |[
      |  {
      |    "name": "test1.xml",
      |    "messageRefId": "messageRefId1",
      |    "reportType": "SingleNewInformation",
      |    "submitted": "2022-02-10T15:35:37.636",
      |    "lastUpdated": "2022-02-10T15:35:37.636",
      |    "status":{"Pending":{}},
      |    "conversationId": "conversationId1"
      |  },
      |  {
      |    "name": "test2.xml",
      |    "messageRefId": "messageRefId2",
      |    "reportType": "SingleNewInformation",
      |    "submitted": "2022-02-10T15:35:37.636",
      |    "lastUpdated": "2022-02-10T15:45:37.636",
      |    "status": {
      |    "Rejected":{
      |      "error":{"fileError":[{"code":"50009","details":"Duplicate message ref ID"}],"recordError":[{"code":"80010","details":"A message can contain either new records (OECD1) or corrections/deletions (OECD2 and OECD3), but cannot contain a mixture of both","docRefIDInError":["asjdhjjhjssjhdjshdAJGSJJS"]}]}
      |      }
      |    },
      |    "conversationId": "conversationId2"
      |  }
      |]""".stripMargin

  private val file: String = """
     |  {
     |    "name": "test3.xml",
     |    "messageRefId": "messageRefId3",
     |    "reportType": "SingleNewInformation",
     |    "submitted": "2022-02-10T15:35:37.636",
     |    "lastUpdated": "2022-02-10T15:45:37.636",
     |    "status": {"Accepted":{}},
     |    "conversationId": "conversationId3"
     |  }""".stripMargin

  private val fileStatus: String = """{"Accepted":{}}""".stripMargin

  "FileDetailsConnector" - {

    "getAllFileDetails" - {

      "must return 'all file details' when getAllFileDetails is successful" in {
        val expectedResult = Some(
          Seq(
            FileDetails(
              testXml1,
              messageRefId1,
              Some(SingleNewInformation),
              LocalDateTime.parse(dateString1),
              LocalDateTime.parse(dateString1),
              Pending,
              conversationId1
            ),
            fileDetails.FileDetails(
              testXml2,
              messageRefId2,
              Some(SingleNewInformation),
              LocalDateTime.parse(dateString1),
              LocalDateTime.parse(dateString2),
              Rejected(
                ValidationErrors(
                  Some(List(FileErrors(MessageRefIDHasAlreadyBeenUsed, Some("Duplicate message ref ID")))),
                  Some(
                    List(
                      RecordError(
                        MessageTypeIndic,
                        Some(
                          "A message can contain either new records (OECD1) or corrections/deletions (OECD2 and OECD3), but cannot contain a mixture of both"
                        ),
                        Some(List("asjdhjjhjssjhdjshdAJGSJJS"))
                      )
                    )
                  )
                )
              ),
              conversationId2
            )
          )
        )

        stubGetResponse(allFilesUrls, OK, allFiles)

        val result = connector.getAllFileDetails

        result.futureValue mustBe expectedResult
      }

      "must return 'None' when getAllFileDetails is successful but response json is invalid" in {

        stubGetResponse(allFilesUrls, OK)

        val result = connector.getAllFileDetails

        result.futureValue mustBe None
      }

      "must return 'None' when getAllFileDetails fails with Error" in {

        val errorCode = errorCodes.sample.value
        stubGetResponse(allFilesUrls, errorCode)

        val result = connector.getAllFileDetails

        result.futureValue mustBe None

      }
    }

    "getFileDetails" - {

      "must return 'file details' when getFileDetails is successful" in {
        val expectedResult = Some(
          fileDetails.FileDetails(
            testXml3,
            messageRefId3,
            Some(SingleNewInformation),
            LocalDateTime.parse(dateString1),
            LocalDateTime.parse(dateString2),
            Accepted,
            conversationId3
          )
        )

        stubGetResponse(fileUrl, OK, file)

        val result = connector.getFileDetails(conversationId3)

        result.futureValue mustBe expectedResult
      }

      "must return 'None' when getFileDetails is successful but response json is invalid" in {

        stubPostResponse(fileUrl, OK)

        val result = connector.getFileDetails(conversationId3)

        result.futureValue mustBe None
      }

      "must return 'None' when getFileDetails fails with Error" in {

        val errorCode = errorCodes.sample.value
        stubPostResponse(fileUrl, errorCode)

        val result = connector.getFileDetails(conversationId3)

        result.futureValue mustBe None

      }
    }

    "getStatus" - {

      "must return 'file status' when getStatus is successful" in {
        val expectedResult = Some(Accepted)

        stubGetResponse(fileStatusUrl, OK, fileStatus)

        val result = connector.getStatus(conversationId3)

        result.futureValue mustBe expectedResult
      }

      "must return 'None' when getStatus is successful but response json is invalid" in {

        stubPostResponse(fileStatusUrl, OK)

        val result = connector.getStatus(conversationId3)

        result.futureValue mustBe None
      }

      "must return 'None' when getStatus fails with Error" in {

        val errorCode = errorCodes.sample.value
        stubPostResponse(fileStatusUrl, errorCode)

        val result = connector.getStatus(conversationId3)

        result.futureValue mustBe None

      }
    }

  }

}
