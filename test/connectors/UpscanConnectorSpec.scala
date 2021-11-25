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
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import models.upscan._
import org.bson.types.ObjectId
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, OK, SERVICE_UNAVAILABLE}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.WireMockHelper

class UpscanConnectorSpec extends SpecBase with GuiceOneAppPerSuite with WireMockHelper with ScalaFutures {

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.upscan.port"                     -> server.port(),
      "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: UpscanConnector = app.injector.instanceOf[UpscanConnector]
  val request: UpscanInitiateRequest  = UpscanInitiateRequest("callbackUrl", "successRedirectUrl", "errorRedirectUrl")

  "getUpscanFormData" - {
    "should return an UpscanInitiateResponse" - {
      "when upscan returns a valid successful response" in {
        val body = PreparedUpload(Reference("Reference"), UploadForm("downloadUrl", Map("formKey" -> "formValue")))
        server.stubFor(
          post(urlEqualTo(connector.upscanInitiatePath))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.toJson(body).toString())
            )
        )

        whenReady(connector.getUpscanFormData) {
          result =>
            result mustBe body.toUpscanInitiateResponse
        }

      }
    }

    "throw an exception" - {
      "when upscan returns a 4xx response" in {
        server.stubFor(
          post(urlEqualTo(connector.upscanInitiatePath))
            .willReturn(
              aResponse()
                .withStatus(BAD_REQUEST)
            )
        )

        val result = connector.getUpscanFormData

        whenReady(result.failed) {
          e =>
            e mustBe an[UpstreamErrorResponse]
            val error = e.asInstanceOf[UpstreamErrorResponse]
            error.statusCode mustBe BAD_REQUEST
        }
      }

      "when upscan returns 5xx response" in {
        server.stubFor(
          post(urlEqualTo(connector.upscanInitiatePath))
            .willReturn(
              aResponse()
                .withStatus(SERVICE_UNAVAILABLE)
            )
        )

        val result = connector.getUpscanFormData
        whenReady(result.failed) {
          e =>
            e mustBe an[UpstreamErrorResponse]
            val error = e.asInstanceOf[UpstreamErrorResponse]
            error.statusCode mustBe SERVICE_UNAVAILABLE
        }
      }
    }
  }

  "getUploadDetails" - {
    "should return an UploadSessionDetails" - {
      "when a valid successful response is returned" in {
        val body = UploadSessionDetails(_id = ObjectId.get(),
                                        uploadId = UploadId("12345"),
                                        reference = Reference("Reference"),
                                        status = UploadedSuccessfully("name", "downloadUrl")
        )
        server.stubFor(
          WireMock
            .get(urlEqualTo("/mandatory-disclosure-rules/upscan/details/12345"))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.toJson(body).toString())
            )
        )

        whenReady(connector.getUploadDetails(UploadId("12345"))) {
          result =>
            result mustBe Some(body)
        }

      }
    }

    "should return None" - {
      "when an invalid response is returned" in {

        server.stubFor(
          WireMock
            .get(urlEqualTo("/mandatory-disclosure-rules/upscan/details/12345"))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.obj().toString())
            )
        )

        whenReady(connector.getUploadDetails(UploadId("12345"))) {
          result =>
            result mustBe None
        }

      }
    }
  }
}
