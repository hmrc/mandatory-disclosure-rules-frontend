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
import pages.HaveSecondContactPage
import play.api.{Logger, Logging}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class SubscriptionService @Inject() (subscriptionConnector: SubscriptionConnector)(implicit ec: ExecutionContext) extends Logging {

  def getContactDetails(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    subscriptionConnector.readSubscription map {
      responseOpt =>
        responseOpt.flatMap {
          responseDetail =>
            populateUserAnswers(responseDetail, userAnswers)
        }
    }

  def updateContactDetails(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    subscriptionConnector.readSubscription flatMap {
      case Some(responseDetails) =>
        RequestDetailForUpdate.convertToRequestDetails(responseDetails, userAnswers) match {
          case Some(requestDetails) => subscriptionConnector.updateSubscription(requestDetails)
          case _ =>
            logger.info("failed to convert userAnswers to RequestDetailForUpdate")
            Future.successful(false)
        }
      case _ =>
        logger.info("readSubscription call failed to fetch the data")
        Future.successful(false)
    }

  private def populateUserAnswers(responseDetail: ResponseDetail, userAnswers: UserAnswers): Option[UserAnswers] =
    populateContactInfo[PrimaryContactDetailsPages](userAnswers, responseDetail.primaryContact, isSecondaryContact = false) map {
      uaWithPrimaryContact =>
        responseDetail.secondaryContact
          .flatMap {
            sc => populateContactInfo[SecondaryContactDetailsPages](uaWithPrimaryContact, sc, isSecondaryContact = true)
          }
          .getOrElse(uaWithPrimaryContact)
    }

  private def populateContactInfo[T <: ContactTypePage](userAnswers: UserAnswers, contactInformation: ContactInformation, isSecondaryContact: Boolean)(implicit
    contactTypePage: T
  ): Option[UserAnswers] = {

    def updateOrgName(userAnswers: UserAnswers): Try[UserAnswers] = contactInformation.contactType match {
      case organisationDetails: OrganisationDetails => userAnswers.set(contactTypePage.contactNamePage, organisationDetails.organisationName)
      case _                                        => Try(userAnswers)
    }

    (for {
      uaWithSecondContact <- userAnswers.set(HaveSecondContactPage, isSecondaryContact)
      uaWithEmail         <- uaWithSecondContact.set(contactTypePage.contactEmailPage, contactInformation.email)
      uaWithTelephone     <- uaWithEmail.set(contactTypePage.contactTelephonePage, contactInformation.phone.getOrElse(""))
      uaWithHaveTelephone <- uaWithTelephone.set(contactTypePage.haveTelephonePage, contactInformation.phone.exists(_.nonEmpty))
      updatedAnswers      <- updateOrgName(uaWithHaveTelephone)
    } yield updatedAnswers).toOption

  }

}
