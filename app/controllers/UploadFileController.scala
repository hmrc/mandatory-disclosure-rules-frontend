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

package controllers

import connectors.UpscanConnector
import controllers.actions._
import forms.UploadFileFormProvider
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import models.upscan._
import pages.UploadIDPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{FileCheckView, JourneyRecoveryStartAgainView, UploadFileView}

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
  view: UploadFileView,
  fileCheckView: FileCheckView,
  errorView: JourneyRecoveryStartAgainView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      toResponse(form)
  }

  private def toResponse(preparedForm: Form[String])(implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Result] =
    (for {
      upscanInitiateResponse <- upscanConnector.getUpscanFormData
      uploadId               <- upscanConnector.requestUpload(upscanInitiateResponse.fileReference)
      updatedAnswers         <- Future.fromTry(request.userAnswers.set(UploadIDPage, uploadId))
      _                      <- sessionRepository.set(updatedAnswers)
    } yield Ok(view(preparedForm, upscanInitiateResponse)))
      .recover {
        case e: Exception =>
          logger.info(s"An exception occurred when contacting Upscan: $e")
          Redirect(routes.ThereIsAProblemController.onPageLoad())
      }

  def showResult: Action[AnyContent] = Action.async {
    implicit uploadResponse =>
      Future.successful(Ok(fileCheckView()))
  }

  def showError(errorCode: String, errorMessage: String, errorRequestId: String): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      errorCode match {
        case "EntityTooLarge" =>
          Future.successful(Redirect(routes.FileTooLargeController.onPageLoad()))
        case "InvalidArgument" =>
          val formWithErrors: Form[String] = form.withError("file-upload", "uploadFile.error.file.empty")
          toResponse(formWithErrors)
        case _ =>
          logger.error(s"Upscan error $errorCode: $errorMessage, requestId is $errorRequestId")
          Future.successful(InternalServerError(errorView()))
      }
  }

  def getStatus: Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      logger.debug("Show status called")
      request.userAnswers.get(UploadIDPage) match {
        case Some(uploadId) =>
          upscanConnector.getUploadStatus(uploadId) flatMap {
            case Some(_: UploadedSuccessfully) =>
              Future.successful(Redirect(routes.FileValidationController.onPageLoad()))
            case Some(r: UploadRejected) =>
              if (r.details.message.contains("octet-stream")) {
                val errorForm: Form[String] = form.withError("file-upload", "uploadFile.error.file.empty")
                logger.debug(s"Show errorForm on rejection $errorForm")
                toResponse(errorForm)
              } else {
                logger.debug(s"Upload rejected. Error details: ${r.details}")
                Future.successful(Redirect(routes.NotXMLFileController.onPageLoad()))
              }
            case Some(Quarantined) =>
              Future.successful(Redirect(routes.VirusFileFoundController.onPageLoad()))
            case Some(Failed) =>
              Future.successful(InternalServerError(errorView()))
            case Some(_) =>
              Future.successful(Ok(fileCheckView()))
            case None =>
              Future.successful(InternalServerError(errorView()))
          }
        case None =>
          Future.successful(InternalServerError(errorView()))
      }
  }
}
