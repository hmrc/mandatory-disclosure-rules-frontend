/*
 * Copyright 2023 HM Revenue & Customs
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

import models.upscan.UpscanURL
import models.{GenericError, InvalidXmlError, MDR401, Message, MessageSpecData, MultipleNewInformation, NonFatalErrors, ValidationErrors}
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class ValidationConnectorSpec extends Connector {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: ValidationConnector    = app.injector.instanceOf[ValidationConnector]
  val validationUrl                          = "/mandatory-disclosure-rules/validate-submission"
  val upscanURL                              = UpscanURL("someUrl")
  val failurePayloadResult: ValidationErrors = ValidationErrors(Seq(GenericError(1, Message("some error")), GenericError(2, Message("another error"))), None)

  "Validation Connector" - {

    "must return a 200 and a Success Object when passing validation" in {

      val expectedBody =
        """{"messageSpecData": {"messageRefId":"XDSG111111","messageTypeIndic":"MDR401","mdrBodyCount":2,"docTypeIndic":"OECD1","reportType":"MultipleNewInformation"}}"""
      val upscanURL = UpscanURL("someUrl")

      stubPostResponse(validationUrl, OK, expectedBody)

      val result = connector.sendForValidation(upscanURL)
      result.futureValue mustBe Right(MessageSpecData("XDSG111111", MDR401, 2, "OECD1", MultipleNewInformation))
    }

    "must return a 200 and a Failure Object when failing validation" in {

      val expectedBody = """
                               |{ "validationErrors": {
                               | "errors":[
                               |     {
                               |         "lineNumber" : 1,
                               |         "message": {
                               |            "messageKey": "some error",
                               |            "args": []
                               |         }
                               |      },
                               |      {
                               |         "lineNumber" : 2,
                               |         "message": {
                               |            "messageKey":"another error",
                               |             "args": []
                               |         }
                               |      }
                               |  ]
                               |}}""".stripMargin

      stubPostResponse(validationUrl, OK, expectedBody)

      val result = connector.sendForValidation(upscanURL)
      result.futureValue mustBe Left(failurePayloadResult)
    }

    "must return a InvalidXmlError when validation returns a Invalid XML in error message" in {
      stubPostResponse(validationUrl, BAD_REQUEST, "Invalid XML")

      val result = connector.sendForValidation(upscanURL)

      val message = s"POST of '${server.baseUrl() + validationUrl}' returned 400 (Bad Request). Response body 'Invalid XML'"

      result.futureValue mustBe Left(InvalidXmlError(message))
    }

    "must return a NonFatalErrors when validation returns a 400 (BAD_REQUEST) status" in {
      stubPostResponse(validationUrl, BAD_REQUEST, "Some error")

      val result = connector.sendForValidation(upscanURL)

      val message = s"POST of '${server.baseUrl() + validationUrl}' returned 400 (Bad Request). Response body 'Some error'"

      result.futureValue mustBe Left(NonFatalErrors(message))
    }
  }

}
