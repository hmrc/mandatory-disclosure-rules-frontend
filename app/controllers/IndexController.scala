/*
 * Copyright 2025 HM Revenue & Customs
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

import connectors.FileDetailsConnector
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import pages.JourneyInProgressPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.SubscriptionService
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  subscriptionService: SubscriptionService,
  fileConnector: FileDetailsConnector,
  view: IndexView
)(implicit
  executionContext: ExecutionContext
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData.apply()) async {
    implicit request =>
      setContactDetailsFlag(request.userAnswers.getOrElse(UserAnswers(request.userId))).flatMap {
        ua =>
          val changeDetailsUrl = request.userType match {
            case Individual   => controllers.routes.ChangeIndividualContactDetailsController.onPageLoad().url
            case Organisation => controllers.routes.ChangeOrganisationContactDetailsController.onPageLoad().url
            case _            => controllers.routes.UnauthorisedController.onPageLoad.url
          }
          subscriptionService.getContactDetails(ua) flatMap {
            case Some(userAnswers) =>
              sessionRepository.set(userAnswers) flatMap {
                _ =>
                  fileConnector.getAllFileDetails map {
                    fileDetails =>
                      Ok(view(request.subscriptionId, routes.UploadFileController.onPageLoad().url, changeDetailsUrl, fileDetails.isDefined))
                  }
              }
            case _ =>
              Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
          }
      }
  }

  private def setContactDetailsFlag(userAnswers: UserAnswers): Future[UserAnswers] =
    Future.fromTry(userAnswers.set(JourneyInProgressPage, true)).flatMap {
      ua =>
        sessionRepository.set(ua).map {
          _ => ua
        }
    }
}
