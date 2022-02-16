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

package models

import base.SpecBase
import play.api.libs.json.Json

import java.time.LocalDateTime

class FileDetailsSpec extends SpecBase {

  "FileDetails" - {
    "Serialise to Json" in {

      val date = LocalDateTime.now

      val error          = FileError("error")
      val fileDetail1    = FileDetails("test1.xml", date, date, Pending, "XGD11111")
      val fileDetail2    = FileDetails("test2.xml", date, date.plusSeconds(11), Rejected(error), "XGD11111")
      val fileDetail3    = FileDetails("test3.xml", date, date.plusSeconds(25), Accepted, "XGD11111")
      val expectedResult = Seq(fileDetail1, fileDetail2, fileDetail3)

      val json = Json.toJson(Seq(fileDetail1, fileDetail2, fileDetail3))

      json.as[Seq[FileDetails]] mustBe expectedResult
    }
  }
}
