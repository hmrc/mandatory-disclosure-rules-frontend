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

package utils

import base.SpecBase
import generators.Generators
import models.{GenericError, Message}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.TableRow

import scala.concurrent.ExecutionContext

class ErrorViewHelperSpec (implicit val ec: ExecutionContext) extends SpecBase with Generators {

  "generateTable" - {

    "must return a table containing multiple rows with line numbers & errors when a given Generic Error" in {

      val mockMultiError: Seq[GenericError] =
        Seq(GenericError(1, Message("Maybe... I don't really wanna know")), GenericError(2, Message("How your garden grows... 'Cause I just wanna fly")))

      val errorHelper = new ErrorViewHelper
      val application = applicationBuilder().build()

      errorHelper.generateTable(mockMultiError)(messages(application)) mustBe
        Seq(
          Seq(
            TableRow(Text("1"), classes = "govuk-table__cell--numeric", attributes = Map("id" -> "lineNumber_1")),
            TableRow(Text("Maybe... I don't really wanna know"), attributes = Map("id" -> "errorMessage_1"))
          ),
          Seq(
            TableRow(Text("2"), classes = "govuk-table__cell--numeric", attributes = Map("id" -> "lineNumber_2")),
            TableRow(Text("How your garden grows... 'Cause I just wanna fly"), attributes = Map("id" -> "errorMessage_2"))
          )
        )
    }
  }
}
