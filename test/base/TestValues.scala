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

package base

import models.{ConversationId, MDR401, MessageSpecData, MultipleNewInformation, ValidatedFileData}

object TestValues {

  val blank: String                    = ""
  val mdrId                            = "MDRID"
  val subscriptionId                   = "subscriptionId"
  val internalId                       = "internalID"
  val inputValue                       = "value"
  val id                               = "id"
  val submit                           = "submit"
  val confirmAndSend                   = "Confirm and send"
  val fileName                         = "test.xml"
  val messageRefId                     = "GDC99999999"
  val docTypeIndic                     = "OECD1"
  val checkSum                         = "1234"
  val fileSize                         = 100L
  val emailId                          = "some@email.com"
  val userAnswer                       = "answer"
  val validContactNumber               = "0928273"
  val conversationId                   = ConversationId("conversationId")
  val errorCode                        = "errorCode"
  val errorMessage                     = "errorMessage"
  val errorReqId                       = "errorReqId"
  val messageSpecData: MessageSpecData = MessageSpecData(messageRefId, MDR401, 2, docTypeIndic, MultipleNewInformation)

  val validatedFileData: ValidatedFileData =
    ValidatedFileData(fileName, messageSpecData, fileSize, checkSum)
}
