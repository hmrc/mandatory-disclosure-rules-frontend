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

import connectors.{UpscanConnector, ValidationConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.upscan.{UploadSessionDetails, UploadedSuccessfully, UpscanURL}
import models.{InvalidXmlError, NormalMode, UserAnswers, ValidatedFileData, ValidationErrors}
import navigation.Navigator
import pages._
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ThereIsAProblemView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileValidationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  val sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  upscanConnector: UpscanConnector,
  requireData: DataRequiredAction,
  validationConnector: ValidationConnector,
  navigator: Navigator,
  errorView: ThereIsAProblemView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private case class ExtractedFileStatus(name: String, downloadUrl: String, size: Option[Long], checkSum: String)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      request.userAnswers
        .get(UploadIDPage)
        .fold {
          logger.error("Cannot find uploadId")
          Future.successful(InternalServerError(errorView()))
        } {
          uploadId =>
            {
              upscanConnector.getUploadDetails(uploadId) map {
                uploadSessions =>
                  getDownloadUrl(uploadSessions).fold {
                    logger.error("File not uploaded successfully")
                    Future.successful(InternalServerError(errorView()))
                  } {
                    downloadDetails =>
                      validationConnector.sendForValidation(UpscanURL(downloadDetails.downloadUrl)) flatMap {
                        case Right(messageSpecData) =>
                          for {
                            updatedAnswers <- Future.fromTry(
                              request.userAnswers.set(ValidXMLPage,
                                                      ValidatedFileData(downloadDetails.name, messageSpecData, downloadDetails.size, downloadDetails.checkSum)
                              )
                            )
                            updatedAnswersWithURL <- Future.fromTry(updatedAnswers.set(URLPage, downloadDetails.downloadUrl))
                            _                     <- sessionRepository.set(updatedAnswersWithURL)
                          } yield Redirect(navigator.nextPage(ValidXMLPage, NormalMode, updatedAnswers))

                        case Left(ValidationErrors(errors, _)) =>
                          for {
                            updatedAnswers           <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, downloadDetails.name))
                            updatedAnswersWithErrors <- Future.fromTry(updatedAnswers.set(GenericErrorPage, errors))
                            _                        <- sessionRepository.set(updatedAnswersWithErrors)
                          } yield Redirect(navigator.nextPage(InvalidXMLPage, NormalMode, updatedAnswers))

                        case Left(InvalidXmlError(_)) =>
                          for {
                            updatedAnswers <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, downloadDetails.name))
                            _              <- sessionRepository.set(updatedAnswers)
                          } yield Redirect(routes.FileErrorController.onPageLoad())

                        case _ =>
                          Future.successful(InternalServerError(errorView()))
                      }
                  }
              }
            }.flatten
        }
  }

  private def getDownloadUrl(uploadSessions: Option[UploadSessionDetails]): Option[ExtractedFileStatus] =
    uploadSessions match {
      case Some(uploadDetails) =>
        uploadDetails.status match {
          case UploadedSuccessfully(name, downloadUrl, size, checkSum) => Some(ExtractedFileStatus(name, downloadUrl, size, checkSum))
          case _                                                       => None
        }
      case _ => None
    }
}
