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

package models.audit

import base.SpecBase
import models.ConversationId
import models.upscan._
import play.api.libs.json.Json

class AuditFileUploadSpec extends SpecBase {

  "AuditFileUpload apply method must create an AuditFileUpload" - {

    "when the file is uploaded successfully" in {
      val fileSize     = 1000L
      val uploadStatus = UploadedSuccessfully("Filename.xml", "URL", fileSize, "checksum")
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "UploadedSuccessfully",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        Some("Filename.xml"),
        Some("1000")
      )
    }

    "when the file uploaded contains a virus" in {
      val uploadStatus = Quarantined
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "Quarantined",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        None,
        None
      )
    }

    "when the file uploaded was rejected" in {
      val uploadStatus = UploadRejected(ErrorDetails("failureReason", "message"))
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "UploadRejected",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        None,
        None
      )
    }

    "when the file uploaded failed for some other reason" in {
      val uploadStatus = Failed
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "Failed",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        None,
        None
      )
    }

    "when the file uploaded was in progress" in {
      val uploadStatus = InProgress
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "InProgress",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        None,
        None
      )
    }

    "when the file uploaded was not started" in {
      val uploadStatus = NotStarted
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))

      result mustBe AuditFileUpload(
        "NotStarted",
        "subscriptionId",
        ConversationId("id"),
        "MDR",
        None,
        None
      )
    }

    "and must serialize AuditFileUpload" in {
      val fileSize     = 1000L
      val uploadStatus = UploadedSuccessfully("Filename.xml", "URL", fileSize, "checksum")
      val result       = AuditFileUpload(uploadStatus, "subscriptionId", UploadId("id"))
      val expectedJson = Json.parse(
        """{
          |"uploadStatus":"UploadedSuccessfully",
          |"subscriptionId":"subscriptionId",
          |"conversationId":"id",
          |"regime":"MDR",
          |"fileName":"Filename.xml",
          |"fileSize":"1000"
          |}""".stripMargin
      )
      Json.toJson(result) mustBe expectedJson
    }

    "and must deserialize AuditFileUpload" in {
      val json = Json.parse(
        """{
          |"uploadStatus":"UploadedSuccessfully",
          |"subscriptionId":"subscriptionId",
          |"conversationId":"id",
          |"regime":"MDR",
          |"fileName":"Filename.xml",
          |"fileSize":"1000"
          |}""".stripMargin
      )
      val expected =
        AuditFileUpload("UploadedSuccessfully", "subscriptionId", ConversationId("id"), "MDR", Some("Filename.xml"), Some("1000"))

      json.as[AuditFileUpload] mustEqual expected
    }
  }
}
