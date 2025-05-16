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

package models.audit

import models.ConversationId
import models.upscan._
import play.api.libs.json.{Json, OFormat}

case class AuditFileUpload(uploadStatus: String,
                           subscriptionId: String,
                           conversationId: ConversationId,
                           regime: String,
                           fileName: Option[String],
                           fileSize: Option[String]
)

object AuditFileUpload {

  implicit val formats: OFormat[AuditFileUpload] = Json.format[AuditFileUpload]

  def apply(uploadStatus: UploadStatus, subscriptionId: String, uploadId: UploadId): AuditFileUpload = {

    val uploadStatusString = uploadStatus match {
      case NotStarted                       => "NotStarted"
      case InProgress                       => "InProgress"
      case Failed                           => "Failed"
      case Quarantined                      => "Quarantined"
      case UploadedSuccessfully(_, _, _, _) => "UploadedSuccessfully"
      case UploadRejected(_)                => "UploadRejected"
    }

    val (name, size) = uploadStatus match {
      case UploadedSuccessfully(name, _, size, _) => (Some(name), Some(size.toString))
      case _                                      => (None, None)
    }

    AuditFileUpload(
      uploadStatusString,
      subscriptionId,
      ConversationId(uploadId.value),
      "MDR",
      name,
      size
    )
  }
}
