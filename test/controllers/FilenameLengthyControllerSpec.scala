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

package controllers

import base.SpecBase
import pages.FilenameLengthyPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.FilenameLengthyView

class FilenameLengthyControllerSpec extends SpecBase {

  private val filename =
    """sample-mdr-file-individual-very_very-very_very-sample-mdr-file-individual-very_very-very_very-sample-mdr-file-individual-
      |very_very-very_very-sample-mdr-file-individual-very_very-very_very""".stripMargin

  "FilenameLengthy Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(FilenameLengthyPage, filename).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.FilenameLengthyController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FilenameLengthyView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(filename)(request, messages(application)).toString
      }
    }
  }
}
