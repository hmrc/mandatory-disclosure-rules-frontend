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

import cats.data.EitherT
import cats.implicits._
import connectors.SubscriptionConnector
import models.UserAnswers
import models.subscription._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class SubscriptionService @Inject() (subscriptionConnector: SubscriptionConnector)(implicit ec: ExecutionContext) {

  def getContactDetails(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[Throwable, UserAnswers]] =
    subscriptionConnector.readSubscription flatMap {
      case Some(details) => populateUserAnswers(details, userAnswers)
      case None =>
        Future.successful(Left(ReadSubscriptionInfoMissing()))
    }

  private def populateUserAnswers(responseDetail: ResponseDetail, userAnswers: UserAnswers): Future[Either[Throwable, UserAnswers]] = {
    for {
      uaWithPrimaryContact <- EitherT.right[Throwable](
        Future.fromTry(populateContactInfo[PrimaryContactDetailsPages](userAnswers, responseDetail.primaryContact.contactInformation))
      )
      userAnswers <- EitherT.right[Throwable](
        Future.fromTry(
          responseDetail.secondaryContact
            .map {
              sc => populateContactInfo[SecondaryContactDetailsPages](uaWithPrimaryContact, sc.contactInformation)
            }
            .getOrElse(Try(uaWithPrimaryContact))
        )
      )
    } yield userAnswers
  }.value

  private def populateContactInfo[T <: ContactTypePage](userAnswers: UserAnswers, contactInformation: Seq[ContactInformation])(implicit
    contactTypePage: T
  ): Try[UserAnswers] =
    contactInformation.head match {
      case contactInformationForOrganisation: ContactInformationForOrganisation =>
        for {
          uaWithContactName <- userAnswers.set(contactTypePage.contactNamePage, contactInformationForOrganisation.organisation.organisationName)
          uaWithEmail       <- uaWithContactName.set(contactTypePage.contactEmailPage, contactInformationForOrganisation.email)
          uaWithTelephone   <- uaWithEmail.set(contactTypePage.contactTelephonePage, contactInformationForOrganisation.phone.getOrElse(""))
        } yield uaWithTelephone
      case contactInformationForIndividual: ContactInformationForIndividual =>
        for {
          uaWithEmail     <- userAnswers.set(contactTypePage.contactEmailPage, contactInformationForIndividual.email)
          uaWithTelephone <- uaWithEmail.set(contactTypePage.contactTelephonePage, contactInformationForIndividual.phone.getOrElse(""))
        } yield uaWithTelephone
    }

}
