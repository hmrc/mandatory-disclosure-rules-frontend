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

package controllers.actions

import base.SpecBase
import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import org.mockito.ArgumentMatchers.any
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier
import utils.RetrievalOps.Ops

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  val keyVal         = "HMRC-MDR-ORG"
  val mdrId          = "MDRID"
  val subscriptionId = "subscriptionId"
  val internalId     = "internalID"

  class Harness(authAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authAction {
      _ => Results.Ok
    }
  }

  "Auth Action" - {

    "when the user hasn't logged in" - {

      "must redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.loginUrl)
        }
      }
    }

    "the user's session has expired" - {

      "must redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value must startWith(appConfig.loginUrl)
        }
      }
    }

    "the user doesn't have sufficient enrolments" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user has an HMRC-MDR-ORG enrolment and internalId" - {

      "must create an IdentifierRequest with a subscriptionId and allow the user to proceed" in {

        type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup]

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val mdrEnrolment = Enrolment(
            key = keyVal,
            identifiers = Seq(EnrolmentIdentifier(mdrId, subscriptionId)),
            state = "",
            delegatedAuthRule = None
          )

          val retrieval: AuthRetrievals = Some(internalId) ~ Enrolments(Set(mdrEnrolment)) ~ Some(Organisation)
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

          val action = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers)
          val controller = action {
            request =>
              val value = request.subscriptionId
              Ok(value)
          }
          val result = controller()(FakeRequest())

          status(result) mustBe OK
          contentAsString(result) mustEqual subscriptionId
        }
      }
    }

    "the user has an HMRC-MDR-ORG enrolment but no MDRID value" - {

      "must redirect the user to register-for-exchange-of-information-frontend" in {

        type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup]

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val mdrEnrolment = Enrolment(
            key = keyVal,
            identifiers = Seq(EnrolmentIdentifier(mdrId, "")),
            state = "",
            delegatedAuthRule = None
          )

          val retrieval: AuthRetrievals = Some(internalId) ~ Enrolments(Set(mdrEnrolment)) ~ Some(Individual)
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

          val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe appConfig.registerUrl
        }
      }
    }

    "the user has no HMRC-MDR-ORG enrolment" - {

      "must redirect the user to register-for-exchange-of-information-frontend" in {

        type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup]

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers                      = application.injector.instanceOf[BodyParsers.Default]
          val appConfig                        = application.injector.instanceOf[FrontendAppConfig]
          val invalidEnrolment                 = "INVALID-ENROLMENT"
          val id                               = "ID"
          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val mdrEnrolment = Enrolment(
            key = invalidEnrolment,
            identifiers = Seq(EnrolmentIdentifier(id, "")),
            state = "",
            delegatedAuthRule = None
          )

          val retrieval: AuthRetrievals = Some(internalId) ~ Enrolments(Set(mdrEnrolment)) ~ Some(Individual)
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

          val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe appConfig.registerUrl
        }
      }
    }

    "the user has no internalId" - {

      "must redirect the user to the unauthorised page" in {

        type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup]

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val mockAuthConnector: AuthConnector = mock[AuthConnector]
          val mdrEnrolment = Enrolment(
            key = keyVal,
            identifiers = Seq(EnrolmentIdentifier(mdrId, subscriptionId)),
            state = "",
            delegatedAuthRule = None
          )

          val retrieval: AuthRetrievals = None ~ Enrolments(Set(mdrEnrolment)) ~ Some(Organisation)
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

          val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user doesn't have sufficient confidence level" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user used an unaccepted auth provider" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.UnauthorisedController.onPageLoad.url
        }
      }
    }

    "the user has an unsupported affinity group" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }
    }

    "the user has an unsupported credential role" - {

      "must redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val appConfig   = application.injector.instanceOf[FrontendAppConfig]

          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), appConfig, bodyParsers)
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(FakeRequest())

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }
      }
    }
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
