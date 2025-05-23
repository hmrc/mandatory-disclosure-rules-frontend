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

package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeFormatUtil {

  val dateFormatter: DateTimeFormatter      = DateTimeFormatter.ofPattern("d MMMM yyyy")
  val timeFormatter: DateTimeFormatter      = DateTimeFormatter.ofPattern("h:mma")
  val smallDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

  def dateFormatted(dateTime: LocalDateTime): String =
    s"${dateTime.toLocalDate.format(smallDateFormatter)} ${dateTime.toLocalTime.format(timeFormatter).toLowerCase}"

}
