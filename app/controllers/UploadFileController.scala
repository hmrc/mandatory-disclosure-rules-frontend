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

package controllers

import connectors.UpscanConnector
import controllers.actions._
import forms.UploadFileFormProvider
import models.requests.DataRequest
import models.upscan._
import pages.UploadIDPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.UploadFileView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UploadFileController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  upscanConnector: UpscanConnector,
  formProvider: UploadFileFormProvider,
  sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  view: UploadFileView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      toResponse(form)
  }

  private def toResponse(preparedForm: Form[String])(implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    val uploadId: UploadId = UploadId.generate
    (for {
      upscanInitiateResponse <- upscanConnector.getUpscanFormData(uploadId)
      uploadId               <- upscanConnector.requestUpload(uploadId, upscanInitiateResponse.fileReference)
      updatedAnswers         <- Future.fromTry(request.userAnswers.set(UploadIDPage, uploadId))
      _                      <- sessionRepository.set(updatedAnswers)
    } yield Ok(view(preparedForm, upscanInitiateResponse)))
      .recover {
        case e: Exception =>
          logger.warn(s"UploadFileController: An exception occurred when contacting Upscan: $e")
          Redirect(routes.ThereIsAProblemController.onPageLoad())
      }
  }

  def showError(errorCode: String, errorMessage: String, errorRequestId: String): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      errorCode match {
        case "EntityTooLarge" =>
          Future.successful(Redirect(routes.FileTooLargeController.onPageLoad()))
        case "InvalidArgument" | "OctetStream" =>
          val formWithErrors: Form[String] = form.withError("file-upload", "uploadFile.error.file.empty")
          toResponse(formWithErrors)
        case _ =>
          logger.warn(s"Upscan error $errorCode: $errorMessage, requestId is $errorRequestId")
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
  }

  def getStatus(uploadId: UploadId): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      upscanConnector.getUploadStatus(uploadId) map {
        case Some(_: UploadedSuccessfully) =>
          Redirect(routes.FileValidationController.onPageLoad().url)
        case Some(r: UploadRejected) =>
          if (r.details.message.contains("octet-stream")) {
            logger.warn(s"Show errorForm on rejection $r")
            val errorReason = r.details.failureReason
            Redirect(routes.UploadFileController.showError("OctetStream", errorReason, "").url)
          } else {
            logger.warn(s"Upload rejected. Error details: ${r.details}")
            Redirect(routes.NotXMLFileController.onPageLoad().url)
          }
        case Some(Quarantined) =>
          Redirect(routes.VirusFileFoundController.onPageLoad().url)
        case Some(Failed) =>
          Redirect(routes.ThereIsAProblemController.onPageLoad().url)
        case Some(_) =>
          Redirect(routes.UploadFileController.getStatus(uploadId).url)
        case None =>
          Redirect(routes.ThereIsAProblemController.onPageLoad().url)
      }
  }
}
