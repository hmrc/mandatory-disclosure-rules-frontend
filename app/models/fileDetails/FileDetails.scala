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

package models.fileDetails

import models.{ConversationId, ReportType}
import play.api.libs.json._

import java.time.LocalDateTime

case class FileDetails(name: String,
                       messageRefId: String,
                       reportType: Option[ReportType],
                       submitted: LocalDateTime,
                       lastUpdated: LocalDateTime,
                       status: FileStatus,
                       conversationId: ConversationId
)

object FileDetails {
  implicit val format: OFormat[FileDetails] = Json.format[FileDetails]

  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = _ compareTo _
}
