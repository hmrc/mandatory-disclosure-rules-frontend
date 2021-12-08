/*
 * Copyright 2021 HM Revenue & Customs
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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.Generators
import models.{GenericError, InvalidXmlError, NonFatalErrors, ValidationErrors}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class ValidationConnectorSpec extends SpecBase with WireMockHelper with Generators with ScalaCheckPropertyChecks with ScalaFutures {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: ValidationConnector = app.injector.instanceOf[ValidationConnector]
  val validationUrl                       = "/mandatory-disclosure-rules/validate-submission"

  val failurePayloadResult: ValidationErrors = ValidationErrors(Seq(GenericError(1, "some error"), GenericError(2, "another error")), None)

  "Validation Connector" - {

    "must return a 200 and a Success Object when passing validation" in {

      val expectedBody = """{"boolean": true}"""

      stubResponse(validationUrl, OK, expectedBody)

      val result = connector.sendForValidation("SomeUrl")
      result.futureValue mustBe Right(true)
    }

    "must return a 200 and a Failure Object when failing validation" in {

      val expectedBody = """
                               |{ "validationErrors": {
                               | "errors":[
                               |     {
                               |         "lineNumber" : 1,
                               |         "messageKey":"some error"
                               |      },
                               |      {
                               |         "lineNumber" : 2,
                               |         "messageKey":"another error"
                               |      }
                               |  ]
                               |}}""".stripMargin

      stubResponse(validationUrl, OK, expectedBody)

      val result = connector.sendForValidation("SomeUrl")
      result.futureValue mustBe Left(failurePayloadResult)
    }

    "must return a InvalidXmlError when validation returns a Invalid XML in error message" in {
      stubResponse(validationUrl, BAD_REQUEST, "Invalid XML")

      val result = connector.sendForValidation("SomeUrl")

      result.futureValue mustBe Left(InvalidXmlError)
    }

    "must return a NonFatalErrors when validation returns a 400 (BAD_REQUEST) status" in {
      stubResponse(validationUrl, BAD_REQUEST, "Some error")

      val result = connector.sendForValidation("SomeUrl")

      val message = s"POST of '${server.baseUrl() + validationUrl}' returned 400 (Bad Request). Response body 'Some error'"

      result.futureValue mustBe Left(NonFatalErrors(message))
    }
  }

  private def stubResponse(expectedUrl: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      post(urlEqualTo(expectedUrl))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )
}
