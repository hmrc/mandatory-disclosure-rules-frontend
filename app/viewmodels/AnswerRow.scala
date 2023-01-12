/*
 * Copyright 2023 HM Revenue & Customs
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

package viewmodels

trait GenericAnswerRow {
  def label: String
  def answer: String
  def answerIsMessageKey: Boolean
  def changeUrl: String
  def hiddenText: Option[String]
  def labelArgs: Seq[String]
  def hiddenTextArgs: Seq[String]
}

case class AnswerRow(
  label: String,
  answer: String,
  answerIsMessageKey: Boolean,
  changeUrl: String,
  hiddenText: Option[String] = None,
  labelArgs: Seq[String] = Nil,
  hiddenTextArgs: Seq[String] = Nil
) extends GenericAnswerRow
