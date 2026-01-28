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

package models.fileDetails

import base.SpecBase
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.*

class FileStatusSpec extends SpecBase {

  "FileStatus JSON format" - {

    "FileStatus JSON format" - {

      "handle Pending" in {
        Json.toJson[FileStatus](Pending) mustEqual Json.obj("Pending" -> Json.obj())
        Json.obj("Pending" -> Json.obj()).as[FileStatus] mustEqual Pending
      }

      "handle Accepted" in {
        Json.toJson[FileStatus](Accepted) mustEqual Json.obj("Accepted" -> Json.obj())
        Json.obj("Accepted" -> Json.obj()).as[FileStatus] mustEqual Accepted
      }

      "handle RejectedSDES" in {
        Json.toJson[FileStatus](RejectedSDES) mustEqual Json.obj("RejectedSDES" -> Json.obj())
        Json.obj("RejectedSDES" -> Json.obj()).as[FileStatus] mustEqual RejectedSDES
      }

      "handle RejectedSDESVirus" in {
        Json.toJson[FileStatus](RejectedSDESVirus) mustEqual Json.obj("RejectedSDESVirus" -> Json.obj())
        Json.obj("RejectedSDESVirus" -> Json.obj()).as[FileStatus] mustEqual RejectedSDESVirus
      }

      "handle Rejected with ValidationErrors" in {
        val errors = ValidationErrors(
          fileError = Some(Seq(FileErrors(FileErrorCode.CustomError, Some("File broken")))),
          recordError = Some(Seq(RecordError(RecordErrorCode.CorrDocRefIdUnknown, Some("Bad record"), Some(Seq("RefId001")))))
        )

        val rejected = Rejected(errors)

        val json = Json.toJson[FileStatus](rejected)

        val expectedJson = Json.obj(
          "Rejected" -> Json.obj(
            "error" -> Json.obj(
              "fileError"   -> Json.arr(Json.obj("code" -> "99999", "details" -> "File broken")),
              "recordError" -> Json.arr(Json.obj("code" -> "80002", "details" -> "Bad record", "docRefIDInError" -> List("RefId001")))
            )
          )
        )

        json mustEqual expectedJson
        json.as[FileStatus] mustEqual rejected
      }

      "fail on unknown FileStatus keys" in {
        Json.obj("Unknown" -> Json.obj()).validate[FileStatus].isError mustBe true
      }

      "fail on non-object JSON" in {
        JsString("Pending").validate[FileStatus].isError mustBe true
      }
    }
  }
}
