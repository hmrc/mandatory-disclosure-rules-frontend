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

package services

import connectors.SubscriptionConnector
import models.UserAnswers
import models.subscription._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class SubscriptionService @Inject() (subscriptionConnector: SubscriptionConnector)(implicit ec: ExecutionContext) {

  def getContactDetails(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    subscriptionConnector.readSubscription map {
      responseOpt =>
        responseOpt.flatMap {
          responseDetail =>
            populateUserAnswers(responseDetail, userAnswers)
        }
    }

  private def populateUserAnswers(responseDetail: ResponseDetail, userAnswers: UserAnswers): Option[UserAnswers] =
    populateContactInfo[PrimaryContactDetailsPages](userAnswers, responseDetail.primaryContact) map {
      uaWithPrimaryContact =>
        responseDetail.secondaryContact
          .flatMap {
            sc => populateContactInfo[SecondaryContactDetailsPages](uaWithPrimaryContact, sc)
          }
          .getOrElse(uaWithPrimaryContact)
    }

  private def populateContactInfo[T <: ContactTypePage](userAnswers: UserAnswers, contactInformation: ContactInformation)(implicit
    contactTypePage: T
  ): Option[UserAnswers] = {

    def updateOrgName(userAnswers: UserAnswers): Try[UserAnswers] = contactInformation.contactType match {
      case organisationDetails: OrganisationDetails => userAnswers.set(contactTypePage.contactNamePage, organisationDetails.organisationName)
      case _                                        => Try(userAnswers)
    }

    (for {
      uaWithEmail         <- userAnswers.set(contactTypePage.contactEmailPage, contactInformation.email)
      uaWithTelephone     <- uaWithEmail.set(contactTypePage.contactTelephonePage, contactInformation.phone.getOrElse(""))
      uaWithHaveTelephone <- uaWithTelephone.set(contactTypePage.haveTelephonePage, contactInformation.phone.exists(_.nonEmpty))
      updatedAnswers      <- updateOrgName(uaWithHaveTelephone)
    } yield updatedAnswers).toOption

  }
}
