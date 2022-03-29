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

package utils

import base.SpecBase
import models.fileDetails.FileErrorCode.{FailedSchemaValidation, MessageRefIDHasAlreadyBeenUsed, UnknownFileErrorCode}
import models.fileDetails.RecordErrorCode.{DocRefIDFormat, MissingCorrDocRefId, UnknownRecordErrorCode}
import models.fileDetails.{FileErrors, RecordError, ValidationErrors}

class FileProblemHelperSpec extends SpecBase {

  "isProblemStatus" - {

    "should return true if errors contain a 'problem' error" in {

      val validationErrors = ValidationErrors(Some(Seq(FileErrors(FailedSchemaValidation, None))), Some(Seq(RecordError(DocRefIDFormat, None, None))))

      FileProblemHelper.isProblemStatus(validationErrors) mustBe true
    }

    "should return true if errors contain an unexpected error code" in {

      val validationErrors =
        ValidationErrors(Some(Seq(FileErrors(UnknownFileErrorCode("12345"), None))), Some(Seq(RecordError(UnknownRecordErrorCode("12345"), None, None))))

      FileProblemHelper.isProblemStatus(validationErrors) mustBe true
    }

    "should return false if validation errors do not contain a 'problem' error" in {

      val validationErrors =
        ValidationErrors(Some(Seq(FileErrors(MessageRefIDHasAlreadyBeenUsed, None))), Some(Seq(RecordError(MissingCorrDocRefId, None, None))))

      FileProblemHelper.isProblemStatus(validationErrors) mustBe false
    }
  }

}
