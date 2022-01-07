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

package models.subscription

import play.api.libs.json.{__, Reads}

sealed trait ContactType

object ContactType {

  implicit lazy val reads: Reads[ContactType] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {
      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    OrganisationDetails.reads or
      IndividualDetails.reads
  }
}

case class OrganisationDetails(organisationName: String) extends ContactType

object OrganisationDetails {

  implicit lazy val reads: Reads[OrganisationDetails] = {
    import play.api.libs.functional.syntax._
    (__ \ "organisation" \ "organisationName").read[String] fmap OrganisationDetails.apply
  }
}

case class IndividualDetails(firstName: String, middleName: Option[String], lastName: String) extends ContactType

object IndividualDetails {
  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[IndividualDetails] =
    (
      (__ \ "individual" \ "firstName").read[String] and
        (__ \ "individual" \ "middleName").readNullable[String] and
        (__ \ "individual" \ "lastName").read[String]
    )(IndividualDetails.apply _)
}

case class ContactInformation(contactType: ContactType, email: String, phone: Option[String], mobile: Option[String])

object ContactInformation {

  implicit lazy val reads: Reads[ContactInformation] = {
    import play.api.libs.functional.syntax._
    (
      __.read[ContactType] and
        (__ \ "email").read[String] and
        (__ \ "phone").readNullable[String] and
        (__ \ "mobile").readNullable[String]
    )(ContactInformation.apply _)
  }

}
