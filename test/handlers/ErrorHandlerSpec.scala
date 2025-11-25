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

package handlers

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

package handlers

import base.SpecBase
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import views.html.{ErrorTemplate, NotFoundTemplate}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class ErrorHandlerSpec extends SpecBase {

  private val messageApi: MessagesApi            = app.injector.instanceOf[MessagesApi]
  private val errorTemplate: ErrorTemplate       = app.injector.instanceOf[ErrorTemplate]
  private val notFoundTemplate: NotFoundTemplate = app.injector.instanceOf[NotFoundTemplate]
  private val errorHandler: ErrorHandler         = new ErrorHandler(messageApi, errorTemplate, notFoundTemplate)
  def fakeRequest                                = FakeRequest("", "")
  def messages: Messages                         = messageApi.preferred(fakeRequest)
  def await[T](futureResult: Future[T]): T       = Await.result(futureResult, 1.seconds)

  "ErrorHandler" - {

    "must return an error page" in {
      val result = await(
        errorHandler.standardErrorTemplate(
          pageTitle = "pageTitle",
          heading = "heading",
          message = "message"
        )(fakeRequest)
      )

      result.body must include("pageTitle")
      result.body must include("heading")
      result.body must include("message")
    }

    "must return a not found template" in {
      val result = await(errorHandler.notFoundTemplate(fakeRequest))
      result.body must include("Report cross-border arrangements for MDR")
      result.body must include("Page not found")
    }
  }
}
