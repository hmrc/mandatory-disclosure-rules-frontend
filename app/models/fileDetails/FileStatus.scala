/*
 * Copyright 2026 HM Revenue & Customs
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

package models.fileDetails
import play.api.libs.json._

sealed trait FileStatus

case object Pending extends FileStatus
case object Accepted extends FileStatus
case object RejectedSDES extends FileStatus
case object RejectedSDESVirus extends FileStatus

case class Rejected(error: ValidationErrors) extends FileStatus {
  override def toString: String = "Rejected"
}

object FileStatus {

  val accepted = "Accepted"
  val rejected = "Rejected"

  implicit val rejectedFormat: OFormat[Rejected] = Json.format[Rejected]

  implicit val format: Format[FileStatus] = new Format[FileStatus] {

    override def reads(json: JsValue): JsResult[FileStatus] =
      json match {
        case JsObject(fields) =>
          fields.toList match {

            case ("Pending", JsObject(_)) :: Nil =>
              JsSuccess(Pending)

            case ("Accepted", JsObject(_)) :: Nil =>
              JsSuccess(Accepted)

            case ("RejectedSDES", JsObject(_)) :: Nil =>
              JsSuccess(RejectedSDES)

            case ("RejectedSDESVirus", JsObject(_)) :: Nil =>
              JsSuccess(RejectedSDESVirus)

            case ("Rejected", obj: JsObject) :: Nil =>
              obj.validate[Rejected]

            case (other, _) :: Nil =>
              JsError(s"Unknown FileStatus: $other")

            case _ =>
              JsError("Invalid FileStatus JSON")
          }

        case _ =>
          JsError("FileStatus must be an object")
      }

    override def writes(fs: FileStatus): JsValue = fs match {

      case Pending =>
        Json.obj("Pending" -> Json.obj())

      case Accepted =>
        Json.obj("Accepted" -> Json.obj())

      case RejectedSDES =>
        Json.obj("RejectedSDES" -> Json.obj())

      case RejectedSDESVirus =>
        Json.obj("RejectedSDESVirus" -> Json.obj())

      case r: Rejected =>
        Json.obj("Rejected" -> Json.toJson(r))
    }
  }
}
