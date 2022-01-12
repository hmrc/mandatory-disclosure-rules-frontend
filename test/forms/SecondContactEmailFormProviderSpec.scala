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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SecondContactEmailFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "secondContactEmail.error.required"
  val invalidKey  = "secondContactEmail.error.invalid"
  val lengthKey   = "secondContactEmail.error.length"
  val maxLength   = 256

  val form = new SecondContactEmailFormProvider()()

  ".email" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validEmailAddressWithinLength(maxLength)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      invalidString = "not a valid email",
      error = FormError(fieldName, invalidKey)
    )

    behave like fieldWithMaxLengthEmail(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}