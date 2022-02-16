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

package models

import play.api.libs.json._

import java.time.LocalDateTime

sealed trait FileStatus

case object Pending extends FileStatus
case object Accepted extends FileStatus

case class Rejected(error: FileError) extends FileStatus {
  override def toString: String            = "Rejected"
  implicit def rejected: OFormat[Rejected] = Json.format[Rejected]
}

object FileStatus {
  implicit def rejected: OFormat[Rejected] = Json.format[Rejected]

  implicit val writes: Writes[FileStatus] = Writes[FileStatus] {
    case Pending            => JsString("Pending")
    case Accepted           => JsString("Accepted")
    case rejected: Rejected => Json.toJson(rejected)
  }

  implicit val reads: Reads[FileStatus] = Reads[FileStatus] {
    case JsString("Pending")  => JsSuccess(Pending)
    case JsString("Accepted") => JsSuccess(Accepted)
    case rejected             => JsSuccess(rejected.as[Rejected])
  }
}

case class FileError(detail: String)

object FileError {
  implicit val format: OFormat[FileError] = Json.format[FileError]
}

case class FileDetails(name: String, submitted: LocalDateTime, lastUpdated: LocalDateTime, status: FileStatus, _id: String)

object FileDetails {
  implicit val format: OFormat[FileDetails] = Json.format[FileDetails]

  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = _ compareTo _
}
