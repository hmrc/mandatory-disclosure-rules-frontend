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

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.SubscriptionService
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  subscriptionService: SubscriptionService,
  view: IndexView
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData.apply) async {
    implicit request =>
      val changeDetailsUrl = request.userType match {
        case Individual   => controllers.routes.ChangeIndividualContactDetailsController.onPageLoad().url
        case Organisation => controllers.routes.ChangeOrganisationContactDetailsController.onPageLoad().url
      }

      subscriptionService.getContactDetails(UserAnswers(request.userId)) flatMap {
        case Some(userAnswers) =>
          sessionRepository.set(userAnswers) map {
            _ =>
              Ok(view(request.subscriptionId, changeDetailsUrl))
          }
        case _ =>
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
  }
}
