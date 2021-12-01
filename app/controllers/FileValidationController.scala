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

import connectors.{UpscanConnector, ValidationConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.upscan.{UploadSessionDetails, UploadedSuccessfully}
import models.{Errors, InvalidXmlError, NormalMode, UserAnswers, ValidationErrors}
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class FileValidationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  val sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  upscanConnector: UpscanConnector,
  requireData: DataRequiredAction,
  validationConnector: ValidationConnector,
  navigator: Navigator
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData() andThen requireData).async {
    implicit request =>
      {
        for {
          uploadId       <- Future.fromTry(Try(request.userAnswers.get(UploadIDPage).getOrElse(throw new RuntimeException("Cannot find uploadId"))))
          uploadSessions <- upscanConnector.getUploadDetails(uploadId)
          (fileName, upScanUrl) = getDownloadUrl(uploadSessions)
          validation: Option[Either[Errors, Boolean]] <- validationConnector.sendForValidation(upScanUrl)
        } yield validation match {
          case Some(Right(_)) =>
            for {
              updatedAnswers        <- Future.fromTry(request.userAnswers.set(ValidXMLPage, fileName))
              updatedAnswersWithURL <- Future.fromTry(updatedAnswers.set(URLPage, upScanUrl))
              _                     <- sessionRepository.set(updatedAnswersWithURL)
            } yield Redirect(navigator.nextPage(ValidXMLPage, NormalMode, updatedAnswers))

          case Some(Left(ValidationErrors(errors, _))) =>
            for {
              updatedAnswers           <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, fileName))
              updatedAnswersWithErrors <- Future.fromTry(updatedAnswers.set(GenericErrorPage, errors))
              _                        <- sessionRepository.set(updatedAnswersWithErrors)
            } yield Redirect(navigator.nextPage(InvalidXMLPage, NormalMode, updatedAnswers))

          case Some(Left(InvalidXmlError)) =>
            for {
              updatedAnswers <- Future.fromTry(UserAnswers(request.userId).set(InvalidXMLPage, fileName))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(InvalidXMLPage, NormalMode, updatedAnswers))

          case _ =>
            Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
        }
      }.flatten
  }

  private def getDownloadUrl(uploadSessions: Option[UploadSessionDetails]) =
    uploadSessions match {
      case Some(uploadDetails) =>
        uploadDetails.status match {
          case UploadedSuccessfully(name, downloadUrl) => (name, downloadUrl)
          case _                                       => throw new RuntimeException("File not uploaded successfully")
        }
      case _ => throw new RuntimeException("File not uploaded successfully")
    }
}
