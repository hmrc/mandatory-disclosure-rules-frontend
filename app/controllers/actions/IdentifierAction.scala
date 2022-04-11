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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised(AuthProviders(GovernmentGateway) and ConfidenceLevel.L50)
      .retrieve(Retrievals.internalId and Retrievals.allEnrolments and Retrievals.affinityGroup) {
        case Some(internalId) ~ enrolments ~ Some(affinity) => getSubscriptionId(request, enrolments, internalId, affinity, block)
        case _ =>
          logger.warn("Unable to retrieve internal id or affinity group")
          throw AuthorisationException.fromString("Unable to retrieve internal id or affinity group")
      } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }

  private def getSubscriptionId[A](request: Request[A],
                                   enrolments: Enrolments,
                                   internalId: String,
                                   affinityGroup: AffinityGroup,
                                   block: IdentifierRequest[A] => Future[Result]
  ): Future[Result] = {

    val mdrEnrolment  = "HMRC-MDR-ORG"
    val mdrIdentifier = "MDRID"

    val subscriptionId: Option[String] = for {
      enrolment      <- enrolments.getEnrolment(mdrEnrolment)
      id             <- enrolment.getIdentifier(mdrIdentifier)
      subscriptionId <- if (id.value.nonEmpty) Some(id.value) else None
    } yield subscriptionId

    subscriptionId.fold {
      logger.warn("Unable to retrieve MDR id from Enrolments")
      Future.successful(Redirect(config.registerUrl))
    } {
      mdrId =>
        block(IdentifierRequest(request, internalId, mdrId, affinityGroup))
    }
  }
}
