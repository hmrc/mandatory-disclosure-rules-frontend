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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import generators.Generators
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class SubmissionConnectorSpec extends SpecBase with WireMockHelper with Generators with ScalaCheckPropertyChecks with ScalaFutures {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: SubmissionConnector = app.injector.instanceOf[SubmissionConnector]
  val submitUrl                           = "/mandatory-disclosure-rules/submit"

  "SubmissionConnector" - {

    "must return a 200 on successful submission of xml" in {

      server.stubFor(
        post(urlEqualTo(submitUrl))
          .willReturn(
            aResponse()
              .withStatus(OK)
          )
      )

      val xml = <test></test>
      whenReady(connector.submitDocument("test-file.xml", "enrolmentID", xml)) {
        result =>
          result.status mustBe OK
      }
    }

    "must return a 400 when submission of xml fails with BadRequest" in {

      server.stubFor(
        post(urlEqualTo(submitUrl))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
          )
      )

      val xml = <test-bad></test-bad>
      whenReady(connector.submitDocument("test-bad-file.xml", "enrolmentID", xml)) {
        result =>
          result.status mustBe BAD_REQUEST
      }
    }

    "must return a 500 when submission of xml fails with InternalServer Error" in {

      server.stubFor(
        post(urlEqualTo(submitUrl))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val xml = <test-error></test-error>
      whenReady(connector.submitDocument("test-file.xml", "enrolmentID", xml)) {
        result =>
          result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

  }

}
