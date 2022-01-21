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

import connectors.{UpscanConnector, ValidationConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.requests.DataRequest
import models.upscan.{UploadSessionDetails, UploadedSuccessfully}
import models.{InvalidXmlError, NormalMode, UserAnswers, ValidationErrors}
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
                      val (fileName, upScanUrl) = downloadDetails
                      validationConnector.sendForValidation(upScanUrl) flatMap {
                        case Right(_) =>
                          for {
                            updatedAnswers        <- Future.fromTry(request.userAnswers.set(ValidXMLPage, fileName))
                            updatedAnswersWithURL <- Future.fromTry(updatedAnswers.set(URLPage, upScanUrl))
                            _                     <- sessionRepository.set(updatedAnswersWithURL)
                          } yield Redirect(navigator.nextPage(ValidXMLPage, NormalMode, updatedAnswers))

                        case Left(ValidationErrors(errors, _)) =>
                          println(s"\n\n\n\n\n\n$errors")
                          for {
                            updatedAnswers           <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, fileName))
                            updatedAnswersWithErrors <- Future.fromTry(updatedAnswers.set(GenericErrorPage, errors))
                            _                        <- sessionRepository.set(updatedAnswersWithErrors)
                          } yield Redirect(navigator.nextPage(InvalidXMLPage, NormalMode, updatedAnswers))

                        case Left(InvalidXmlError(_)) =>
                          println(s"\n\n\n\n\n\nINVALID")
                          for {
                            updatedAnswers <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, fileName))
                            _              <- sessionRepository.set(updatedAnswers)
                          } yield Redirect(navigator.nextPage(InvalidXMLPage, NormalMode, updatedAnswers))

                        case e =>
                          println(s"ERRRROR\n\n\n\n\n\n$e")
                          Future.successful(InternalServerError(errorView()))
                      }
                  }
              }
            }.flatten
        }
  }

  private def getDownloadUrl(uploadSessions: Option[UploadSessionDetails])(implicit request: DataRequest[_]): Option[(String, String)] =
    uploadSessions match {
      case Some(uploadDetails) =>
        uploadDetails.status match {
          case UploadedSuccessfully(name, downloadUrl) => Some(name, downloadUrl)
          case _                                       => None
        }
      case _ => None
    }
}
