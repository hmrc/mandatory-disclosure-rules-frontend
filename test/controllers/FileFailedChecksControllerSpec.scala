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
import models.{ConversationId, MDR401, MessageSpecData, MultipleNewInformation, ValidatedFileData}
import pages.{ConversationIdPage, ValidXMLPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileCheckViewModel
import views.html.{FileFailedChecksView, ThereIsAProblemView}

class FileFailedChecksControllerSpec extends SpecBase {

  val fileSize = 1000L

  "FileFailedChecks Controller" - {

    "must return OK and the correct view for a GET" in {

      val conversationId  = ConversationId("conversationId")
      val validXmlDetails = ValidatedFileData("name", MessageSpecData("messageRefId", MDR401, 2, "OECD1", MultipleNewInformation), fileSize, "1234")

      val userAnswers = emptyUserAnswers
        .set(ValidXMLPage, validXmlDetails)
        .success
        .value
        .set(ConversationIdPage, conversationId)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {

        val fileSummaryList = FileCheckViewModel.createFileSummary(validXmlDetails.fileName, "Rejected")(messages(application))
        val action          = routes.FileRejectedController.onPageLoadSlow(conversationId).url
        val request         = FakeRequest(GET, routes.FileFailedChecksController.onPageLoad().url)
        val result          = route(application, request).value
        val view            = application.injector.instanceOf[FileFailedChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action)(request, messages(application)).toString
      }
    }

    "must throw an internal server error when no file data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, routes.FileFailedChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[ThereIsAProblemView]

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
