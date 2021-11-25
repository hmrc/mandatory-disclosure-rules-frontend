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

package controllers

import connectors.UpscanConnector
import controllers.actions._
import forms.UploadFileFormProvider
import models.UserAnswers
import models.requests.OptionalDataRequest
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

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      toResponse(form)
  }

  private def toResponse(preparedForm: Form[String])(implicit request: OptionalDataRequest[AnyContent], hc: HeaderCarrier): Future[Result] =
    (for {
      upscanInitiateResponse <- upscanConnector.getUpscanFormData
      uploadId               <- upscanConnector.requestUpload(upscanInitiateResponse.fileReference)
      updatedAnswers         <- Future.fromTry(UserAnswers(request.userId).set(UploadIDPage, uploadId))
      _                      <- sessionRepository.set(updatedAnswers)
    } yield Ok(view(preparedForm, upscanInitiateResponse)))
      .recover {
        case _: Exception => throw new UpscanTimeoutException
      }

  def showResult: Action[AnyContent] = Action.async {
    implicit uploadResponse =>
      Future.successful(Ok(fileCheckView()))
  }

  def showError(errorCode: String, errorMessage: String, errorRequestId: String): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      errorCode match {
        case "EntityTooLarge" =>
          Future.successful(Redirect(routes.FileTooLargeController.onPageLoad()))
        case "InvalidArgument" =>
          val formWithErrors: Form[String] = form.withError("file", "uploadFile.error.file.empty")
          toResponse(formWithErrors)
        case _ =>
          logger.error(s"Upscan error $errorCode: $errorMessage, requestId is $errorRequestId")
          Future.successful(InternalServerError(errorView()))
      }
  }

  def getStatus: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>
      logger.debug("Show status called")
      request.userAnswers.flatMap(_.get(UploadIDPage)) match {
        case Some(uploadId) =>
          upscanConnector.getUploadStatus(uploadId) flatMap {
            case Some(_: UploadedSuccessfully) =>
              Future.successful(Redirect(routes.FileValidationController.onPageLoad()))
            case Some(r: UploadRejected) =>
              val errorMessage = if (r.details.message.contains("octet-stream")) {
                "uploadFile.error.file.empty"
              } else {
                "uploadFile.error.file.invalid"
              }
              val errorForm: Form[String] = form.withError("file", errorMessage)
              logger.debug(s"Show errorForm on rejection $errorForm")
              toResponse(errorForm)
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