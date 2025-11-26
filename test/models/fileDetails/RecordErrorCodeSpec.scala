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

package models.fileDetails

import base.SpecBase
import play.api.libs.json.*

class RecordErrorCodeSpec extends SpecBase {

  "RecordErrorCode JSON Reads" - {

    "must read all known error codes correctly" in {
      val mappings = Seq(
        "80000" -> RecordErrorCode.DocRefIDAlreadyUsed,
        "80001" -> RecordErrorCode.DocRefIDFormat,
        "80002" -> RecordErrorCode.CorrDocRefIdUnknown,
        "80003" -> RecordErrorCode.CorrDocRefIdNoLongerValid,
        "80004" -> RecordErrorCode.CorrDocRefIdForNewData,
        "80005" -> RecordErrorCode.MissingCorrDocRefId,
        "80008" -> RecordErrorCode.ResendOption,
        "80009" -> RecordErrorCode.DeleteParentRecord,
        "80010" -> RecordErrorCode.MessageTypeIndic,
        "80011" -> RecordErrorCode.CorrDocRefIDTwiceInSameMessage,
        "80013" -> RecordErrorCode.UnknownDocRefID,
        "80014" -> RecordErrorCode.DocRefIDIsNoLongerValid,
        "99999" -> RecordErrorCode.CustomError
      )

      for ((code, expected) <- mappings)
        Json.fromJson[RecordErrorCode](JsString(code)).asEither mustBe Right(expected)
    }

    "must create UnknownRecordErrorCode for unknown codes" in {
      val json = JsString("12345")
      Json.fromJson[RecordErrorCode](json).asEither mustBe Right(
        RecordErrorCode.UnknownRecordErrorCode("12345")
      )
    }
  }

  "RecordErrorCode JSON Writes" - {

    "must write known error codes as their string code" in {
      RecordErrorCode.values.foreach {
        value =>
          Json.toJson(value) mustBe JsString(value.code)
      }
    }

    "must write UnknownRecordErrorCode as its underlying code" in {
      val unknown = RecordErrorCode.UnknownRecordErrorCode("ABCDE")
      Json.toJson[RecordErrorCode](unknown) mustBe JsString("ABCDE")
    }
  }

  "RecordErrorCode round-trip JSON conversion" - {

    "must correctly convert to JSON and back for all known values" in {
      RecordErrorCode.values.foreach {
        value =>
          val json = Json.toJson(value)
          json.validate[RecordErrorCode].asEither mustBe Right(value)
      }
    }
  }

  "RecordErrorCode.values" - {
    "must contain all declared case objects" in {

      RecordErrorCode.values must contain theSameElementsAs Seq(
        RecordErrorCode.DocRefIDAlreadyUsed,
        RecordErrorCode.DocRefIDFormat,
        RecordErrorCode.CorrDocRefIdUnknown,
        RecordErrorCode.CorrDocRefIdNoLongerValid,
        RecordErrorCode.CorrDocRefIdForNewData,
        RecordErrorCode.MissingCorrDocRefId,
        RecordErrorCode.ResendOption,
        RecordErrorCode.DeleteParentRecord,
        RecordErrorCode.MessageTypeIndic,
        RecordErrorCode.CorrDocRefIDTwiceInSameMessage,
        RecordErrorCode.UnknownDocRefID,
        RecordErrorCode.DocRefIDIsNoLongerValid,
        RecordErrorCode.CustomError
      )
    }
  }
}
