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

package generators

import models.fileDetails.RecordErrorCode.CustomError
import models.fileDetails._
import models.subscription._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit val arbitraryOrganisationDetails: Arbitrary[OrganisationDetails] = Arbitrary {
    for {
      orgName <- arbitrary[String]
    } yield OrganisationDetails(orgName)
  }

  implicit val arbitraryIndividualDetails: Arbitrary[IndividualDetails] = Arbitrary {
    for {
      firstName  <- arbitrary[String]
      middleName <- Gen.option(arbitrary[String])
      lastName   <- arbitrary[String]
    } yield IndividualDetails(firstName, middleName, lastName)
  }

  implicit val arbitraryContactType: Arbitrary[ContactType] = Arbitrary {
    Gen.oneOf[ContactType](arbitrary[OrganisationDetails], arbitrary[IndividualDetails])
  }

  implicit val arbitraryContactInformation: Arbitrary[ContactInformation] = Arbitrary {
    for {
      contactType <- arbitrary[ContactType]
      email       <- arbitrary[String]
      phone       <- Gen.option(arbitrary[String])
      mobile      <- Gen.option(arbitrary[String])
    } yield ContactInformation(contactType, email, phone, mobile)
  }

  implicit val arbitraryRequestDetail: Arbitrary[RequestDetailForUpdate] = Arbitrary {
    for {
      idType           <- arbitrary[String]
      idNumber         <- arbitrary[String]
      tradingName      <- Gen.option(arbitrary[String])
      isGBUser         <- arbitrary[Boolean]
      primaryContact   <- arbitrary[ContactInformation]
      secondaryContact <- Gen.option(arbitrary[ContactInformation])
    } yield RequestDetailForUpdate(idType, idNumber, tradingName, isGBUser, primaryContact, secondaryContact)
  }

  implicit val arbitraryResponseDetail: Arbitrary[ResponseDetail] = Arbitrary {
    for {
      subscriptionID   <- arbitrary[String]
      tradingName      <- Gen.option(arbitrary[String])
      isGBUser         <- arbitrary[Boolean]
      primaryContact   <- arbitrary[ContactInformation]
      secondaryContact <- Gen.option(arbitrary[ContactInformation])
    } yield ResponseDetail(subscriptionID, tradingName, isGBUser, primaryContact, secondaryContact)
  }

  implicit val arbitraryFileErrorCode: Arbitrary[FileErrorCode] = Arbitrary {
    Gen.oneOf[FileErrorCode](FileErrorCode.values)
  }

  implicit val arbitraryRecordErrorCode: Arbitrary[RecordErrorCode] = Arbitrary {
    Gen.oneOf[RecordErrorCode](RecordErrorCode.values.filterNot(_ == CustomError))
  }

  implicit val arbitraryUpdateFileErrors: Arbitrary[FileErrors] = Arbitrary {
    for {
      fileErrorCode <- arbitrary[FileErrorCode]
      details       <- Gen.option(arbitrary[String])
    } yield FileErrors(fileErrorCode, details)
  }

  implicit val arbitraryUpdateRecordErrors: Arbitrary[RecordError] = Arbitrary {
    for {
      recordErrorCode <- arbitrary[RecordErrorCode]
      details         <- Gen.option(arbitrary[String])
      docRefIdRef     <- Gen.option(listWithMaxLength(5, arbitrary[String]))
    } yield RecordError(recordErrorCode, details, docRefIdRef)
  }

  implicit val arbitraryUpdateValidationErrors: Arbitrary[ValidationErrors] =
    Arbitrary {
      for {
        fileErrors   <- Gen.option(listWithMaxLength(5, arbitrary[FileErrors]))
        recordErrors <- Gen.option(listWithMaxLength(5, arbitrary[RecordError]))
      } yield ValidationErrors(fileErrors, recordErrors)
    }

  def listWithMaxLength[T](maxSize: Int, gen: Gen[T]): Gen[Seq[T]] =
    for {
      size  <- Gen.choose(1, maxSize)
      items <- Gen.listOfN(size, gen)
    } yield items

}
