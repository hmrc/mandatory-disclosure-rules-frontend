/*
 * Copyright 2026 HM Revenue & Customs
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

import models.upscan._
import org.bson.types.ObjectId
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, OK, SERVICE_UNAVAILABLE}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.UpstreamErrorResponse

class UpscanConnectorSpec extends Connector {

  val fileSize             = 100L
  val uploadId: UploadId   = UploadId("12345")
  val upScanDetailsUrl     = "/mandatory-disclosure-rules/upscan/details/12345"
  val upScanStatusUrl      = "/mandatory-disclosure-rules/upscan/status/12345"
  val callbackUrl          = "callbackUrl"
  val downloadUrl          = "downloadUrl"
  val formKey              = "formKey"
  val formValue            = "formValue"
  val checksum             = "1234"
  val reference: Reference = Reference("Reference")
  val name                 = "name"

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.upscan.port"                     -> server.port(),
      "microservice.services.mandatory-disclosure-rules.port" -> server.port()
    )
    .build()

  lazy val connector: UpscanConnector = app.injector.instanceOf[UpscanConnector]
  val request: UpscanInitiateRequest  = UpscanInitiateRequest(callbackUrl)

  "getUpscanFormData" - {
    "should return an UpscanInitiateResponse" - {
      "when upscan returns a valid successful response" in {
        val body = PreparedUpload(reference, UploadForm(downloadUrl, Map(formKey -> formValue)))

        stubPostResponse(connector.upscanInitiatePath, OK, Json.toJson(body).toString())

        whenReady(connector.getUpscanFormData(uploadId)) {
          result =>
            result mustBe body.toUpscanInitiateResponse
        }

      }
    }

    "throw an exception" - {
      "when upscan returns a 4xx response" in {
        stubPostResponse(connector.upscanInitiatePath, BAD_REQUEST)

        val result = connector.getUpscanFormData(uploadId)

        whenReady(result.failed) {
          e =>
            e mustBe an[UpstreamErrorResponse]
            val error = e.asInstanceOf[UpstreamErrorResponse]
            error.statusCode mustBe BAD_REQUEST
        }
      }

      "when upscan returns 5xx response" in {
        stubPostResponse(connector.upscanInitiatePath, SERVICE_UNAVAILABLE)

        val result = connector.getUpscanFormData(uploadId)
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
                                        uploadId = uploadId,
                                        reference = reference,
                                        status = UploadedSuccessfully(name, downloadUrl, fileSize, checksum)
        )

        stubGetResponse(upScanDetailsUrl, OK, Json.toJson(body).toString())

        whenReady(connector.getUploadDetails(uploadId)) {
          result =>
            result mustBe Some(body)
        }

      }
    }

    "should return None" - {
      "when an invalid response is returned" in {
        stubGetResponse(upScanDetailsUrl, OK, Json.obj().toString())

        whenReady(connector.getUploadDetails(uploadId)) {
          result =>
            result mustBe None
        }

      }

      "when an BAD_REQUEST response is returned" in {
        stubGetResponse(upScanDetailsUrl, BAD_REQUEST, Json.obj().toString())

        whenReady(connector.getUploadDetails(uploadId)) {
          result =>
            result mustBe None
        }

      }
    }
  }

  "getUploadStatus" - {
    "should return an UploadStatus for a valid UploadId" - {
      "when an UploadedSuccessfully response is returned" in {
        val body =
          """{
            | "_type": "UploadedSuccessfully",
            | "name": "name",
            | "downloadUrl": "downloadUrl",
            | "size": 100,
            | "checkSum":"1234"
            | }
            |""".stripMargin

        stubGetResponse(upScanStatusUrl, OK, body)

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe Some(UploadedSuccessfully(name, downloadUrl, fileSize, checksum))
        }
      }

      "when a NotStarted response is returned" in {

        val body =
          """{
            | "_type": "NotStarted"
            | }
            |""".stripMargin

        stubGetResponse(upScanStatusUrl, OK, body)

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe Some(NotStarted)
        }
      }

      "when a InProgress response is returned" in {

        val body =
          """{
            | "_type": "InProgress"
            | }
            |""".stripMargin

        stubGetResponse(upScanStatusUrl, OK, body)

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe Some(InProgress)
        }
      }

      "when a Failed response is returned" in {

        val body =
          """{
            | "_type": "Failed"
            | }
            |""".stripMargin

        stubGetResponse(upScanStatusUrl, OK, body)

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe Some(Failed)
        }
      }

      "when a Quarantined response is returned" in {

        val body =
          """{
            | "_type": "Quarantined"
            | }
            |""".stripMargin

        stubGetResponse(upScanStatusUrl, OK, body)

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe Some(Quarantined)
        }
      }
    }

    "should return None" - {
      "when an invalid response is returned" in {
        stubGetResponse(upScanStatusUrl, OK, Json.obj().toString())

        whenReady(connector.getUploadStatus(uploadId)) {
          result =>
            result mustBe None
        }

      }
    }
  }
}
