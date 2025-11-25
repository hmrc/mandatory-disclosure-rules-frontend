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

package models.upscan

import base.SpecBase
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.*
import play.api.mvc.QueryStringBindable

import java.util.UUID

class UploadIdSpec extends SpecBase {

  private val binder = implicitly[QueryStringBindable[UploadId]]

  "UploadId" - {

    "generate must produce a valid UUID string" in {
      val id = UploadId.generate
      noException should be thrownBy UUID.fromString(id.value)
    }

    "JSON Writes (string writer) must produce a JsString" in {
      val id = UploadId("abc-123")
      Json.toJson(id)(UploadId.writesUploadId) mustEqual JsString("abc-123")
    }

    "JSON Reads (string reader) must read from a JsString" in {
      val json = JsString("xyz-987")
      json.as[UploadId](UploadId.readsUploadId) mustEqual UploadId("xyz-987")
    }

    "JSON Reads must fail for non-string JSON" in {
      val result = JsNumber(42).validate[UploadId](UploadId.readsUploadId)
      result.isError mustBe true
    }

    "OFormat must read/write object JSON" in {
      val id   = UploadId("object-id-test")
      val json = Json.toJson(id)(UploadId.uploadIdFormat)

      json mustEqual Json.obj("value" -> "object-id-test")
      json.as[UploadId](UploadId.uploadIdFormat) mustEqual id
    }

    "QueryStringBindable must bind and unbind UploadId" in {
      val original = UploadId("bind-test-123")

      val bound =
        binder.bind("uploadId", Map("uploadId" -> Seq(original.value)))

      bound mustEqual Some(Right(original))

      binder.unbind("uploadId", original) mustEqual s"uploadId=${original.value}"
    }
  }
}
