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

package viewmodels

import base.SpecBase
import models.fileDetails._
import uk.gov.hmrc.govukfrontend.views.Aliases.{TableRow, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table}

class FileRejectedViewModelSpec extends SpecBase {

  val header = Some(
    List(
      table.HeadCell(Text("Code"), None, "", None, None, Map()),
      HeadCell(Text("DocRefId"), None, "govuk-!-width-one-half", None, None, Map()),
      HeadCell(Text("Error"), None, "govuk-!-width-one-half", None, None, Map())
    )
  )

  "FileRejectedViewModel" - {
    "create table for all the allowed error codes except custom errors " in {

      val fileErrors =
        Some(Seq(FileErrors(FileErrorCode.MessageRefIDHasAlreadyBeenUsed, None), FileErrors(FileErrorCode.FileContainsTestDataForProductionEnvironment, None)))

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.DocRefIDAlreadyUsed, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdUnknown, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdNoLongerValid, None, None),
          RecordError(RecordErrorCode.CorrDocRefIdForNewData, None, None),
          RecordError(RecordErrorCode.MissingCorrDocRefId, None, None),
          RecordError(RecordErrorCode.ResendOption, None, None),
          RecordError(RecordErrorCode.DeleteParentRecord, None, None),
          RecordError(RecordErrorCode.MessageTypeIndic, None, None),
          RecordError(RecordErrorCode.CorrDocRefIDTwiceInSameMessage, None, None),
          RecordError(RecordErrorCode.UnknownDocRefID, None, None),
          RecordError(RecordErrorCode.DocRefIDIsNoLongerValid, None, None)
        )
      )
      val validationErrors = ValidationErrors(fileErrors, recordErrors)

      val expectedTable =
        Table(
          List(
            List(
              TableRow(Text("509"), None, "", None, None, Map("id" -> "code_509")),
              TableRow(Text("N/A"), None, "", None, None, Map("id" -> "docRefId_509")),
              TableRow(
                HtmlContent("The MessageRefId has already been used in a file previously received, it must be unique"),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_509")
              )
            ),
            List(
              TableRow(Text("510"), None, "", None, None, Map("id" -> "code_510")),
              TableRow(Text("N/A"), None, "", None, None, Map("id" -> "docRefId_510")),
              TableRow(
                HtmlContent("We cannot accept test data so each DocTypeIndic must have a value of either OECD0, OECD1, OECD2 or OECD3"),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_510")
              )
            ),
            List(
              TableRow(Text("800"), None, "", None, None, Map("id" -> "code_800")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_800")),
              TableRow(
                HtmlContent(
                  "DocRefId values must be unique for:<ol class ='govuk-list govuk-list--bullet govuk-!-margin-top-2 govuk-!-margin-bottom-0'><li>the Disclosing section and MdrReport section within the same report (MdrBody) in this file</li><li>each report (MdrBody) in this file</li><li>this file compared to a previously accepted file for the same or a different arrangement</li></ol>"
                ),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_800")
              )
            ),
            List(
              TableRow(Text("802"), None, "", None, None, Map("id" -> "code_802")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_802")),
              TableRow(HtmlContent("The CorrDocRefId does not match any DocRefId previously received"), None, "", None, None, Map("id" -> "errorMessage_802"))
            ),
            List(
              TableRow(Text("803"), None, "", None, None, Map("id" -> "code_803")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_803")),
              TableRow(HtmlContent("The CorrDocRefId is for a section that has already been corrected or deleted"),
                       None,
                       "",
                       None,
                       None,
                       Map("id" -> "errorMessage_803")
              )
            ),
            List(
              TableRow(Text("804"), None, "", None, None, Map("id" -> "code_804")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_804")),
              TableRow(HtmlContent("Sections that contain new or resent information must not have a CorrDocRefId"),
                       None,
                       "",
                       None,
                       None,
                       Map("id" -> "errorMessage_804")
              )
            ),
            List(
              TableRow(Text("805"), None, "", None, None, Map("id" -> "code_805")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_805")),
              TableRow(HtmlContent("This section contains a correction or deletion so it must contain a CorrDocRefId"),
                       None,
                       "",
                       None,
                       None,
                       Map("id" -> "errorMessage_805")
              )
            ),
            List(
              TableRow(Text("808"), None, "", None, None, Map("id" -> "code_808")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_808")),
              TableRow(
                HtmlContent(
                  "Resend option (OECD0) must only be used for the Disclosing element, not for the MdrReport element. Ensure the MdrReport DocTypeIndic contains one of the allowed values"
                ),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_808")
              )
            ),
            List(
              TableRow(Text("809"), None, "", None, None, Map("id" -> "code_809")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_809")),
              TableRow(
                HtmlContent("This Disclosing section can only be deleted if the MdrReport section linked to it is also deleted"),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_809")
              )
            ),
            List(
              TableRow(Text("810"), None, "", None, None, Map("id" -> "code_810")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_810")),
              TableRow(
                HtmlContent(
                  "The file cannot contain a combination of new information (DocTypeIndic: OECD1) and corrections or deletions (DocTypeIndic: OECD2 or OECD3).<p>The MessageTypeIndic must be compatible with each DocTypeIndic in the file.</p><p>If the MessageTypeIndic is MDR401 for new information, then every DocTypeIndic must be OECD1.</p><p class='remove-end-whitespace'>If the MessageTypeIndic is MDR402 for corrections or deletions, then the DocTypeIndic values must be either OECD2, OECD3 or OECD0.</p>"
                ),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_810")
              )
            ),
            List(
              TableRow(Text("811"), None, "", None, None, Map("id" -> "code_811")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_811")),
              TableRow(HtmlContent("A CorrDocRefId value must not be used more than once in the same file"),
                       None,
                       "",
                       None,
                       None,
                       Map("id" -> "errorMessage_811")
              )
            ),
            List(
              TableRow(Text("813"), None, "", None, None, Map("id" -> "code_813")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_813")),
              TableRow(
                HtmlContent(
                  "The Disclosing DocTypeIndic of OECD0 indicates this section contains resent information, but the DocRefId does not match any we have received"
                ),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_813")
              )
            ),
            List(
              TableRow(Text("814"), None, "", None, None, Map("id" -> "code_814")),
              TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_814")),
              TableRow(
                HtmlContent(
                  "The Disclosing DocTypeIndic of OECD0 shows this section contains resent information, but the DocRefId is for information that has since been corrected or deleted. Provide the DocRefId of the section you want to correct"
                ),
                None,
                "",
                None,
                None,
                Map("id" -> "errorMessage_814")
              )
            )
          ),
          header,
          Some("Errors"),
          "govuk-table__caption govuk-heading-m",
          false,
          "",
          Map()
        )

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

    "create table for all the allowed the custom errors " in {

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_901), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_902), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_903), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_904a), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_905), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_906), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_907), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_908), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_909), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_910), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_911), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_912), None)
        )
      )

      val expectedTable = Table(
        List(
          List(
            TableRow(Text("901"), None, "", None, None, Map("id" -> "code_901")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_901")),
            TableRow(
              HtmlContent(
                "The CorrDocRefId does not match a DocRefId from the same type of section (either Disclosing or MdrReport). It must refer to the same element"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_901")
            )
          ),
          List(
            TableRow(Text("902"), None, "", None, None, Map("id" -> "code_902")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_902")),
            TableRow(
              HtmlContent("The MdrReport CorrDocRefId does not refer to the same previously sent MdrBody as the Disclosing element"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_902")
            )
          ),
          List(
            TableRow(Text("903"), None, "", None, None, Map("id" -> "code_903")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_903")),
            TableRow(
              HtmlContent("The Disclosing section contains resent data (DocTypeIndic = OECD0) so it must not have a CorrDocRefId"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_903")
            )
          ),
          List(
            TableRow(Text("904"), None, "", None, None, Map("id" -> "code_904")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_904")),
            TableRow(
              HtmlContent("MdrReport CrsAvoidance or OOS Reason is not one of the allowed values for the Disclosing Capacity provided"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_904")
            )
          ),
          List(
            TableRow(Text("905"), None, "", None, None, Map("id" -> "code_905")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_905")),
            TableRow(
              HtmlContent("Since the DocTypeIndic of Disclosing is OECD0, the DocTypeIndic of MdrReport must be OECD2"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_905")
            )
          ),
          List(
            TableRow(Text("906"), None, "", None, None, Map("id" -> "code_906")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_906")),
            TableRow(
              HtmlContent(
                "Since the MdrReport has a DocTypeIndic of OECD3, indicating this section must be deleted, this Disclosing section must be deleted too"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_906")
            )
          ),
          List(
            TableRow(Text("907"), None, "", None, None, Map("id" -> "code_907")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_907")),
            TableRow(
              HtmlContent(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, the Disclosing DocTypeIndic must contain the value of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_907")
            )
          ),
          List(
            TableRow(Text("908"), None, "", None, None, Map("id" -> "code_908")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_908")),
            TableRow(
              HtmlContent(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, an MdrReport section must be provided with a DocTypeIndic of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_908")
            )
          ),
          List(
            TableRow(Text("909"), None, "", None, None, Map("id" -> "code_909")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_909")),
            TableRow(
              HtmlContent("DocRefId must be 100 characters or less, start with your 15-character MDR ID and include up to 85 other characters of your choice"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_909")
            )
          ),
          List(
            TableRow(Text("910"), None, "", None, None, Map("id" -> "code_910")),
            TableRow(HtmlContent("N/A"), None, "", None, None, Map("id" -> "docRefId_910")),
            TableRow(
              HtmlContent(
                "MessageRefId must be 85 characters or less, start with your 15-character MDR ID and include up to 70 other characters of your choice"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_910")
            )
          ),
          List(
            TableRow(Text("911"), None, "", None, None, Map("id" -> "code_911")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_911")),
            TableRow(HtmlContent("Provide an issuedBy for every TIN that has a value other than NOTIN"), None, "", None, None, Map("id" -> "errorMessage_911"))
          ),
          List(
            TableRow(Text("912"), None, "", None, None, Map("id" -> "code_912")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_912")),
            TableRow(HtmlContent("The top level of the StructureChart must not have an Ownership or InvestAmount"),
                     None,
                     "",
                     None,
                     None,
                     Map("id" -> "errorMessage_912")
            )
          )
        ),
        header,
        Some("Errors"),
        "govuk-table__caption govuk-heading-m",
        false,
        "",
        Map()
      )

      val validationErrors = ValidationErrors(None, recordErrors)

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

    "create table for all the allowed the custom errors when the errors have extra content at the end" in {

      val recordErrors = Some(
        Seq(
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_901 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_902 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_903 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_904a + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_905 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_906 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_907 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_908 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_909 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_910 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_911 + ". extra content"), None),
          RecordError(RecordErrorCode.CustomError, Some(FileRejectedViewModel.error_details_912 + ". extra content"), None)
        )
      )

      val expectedTable = Table(
        List(
          List(
            TableRow(Text("901"), None, "", None, None, Map("id" -> "code_901")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_901")),
            TableRow(
              HtmlContent(
                "The CorrDocRefId does not match a DocRefId from the same type of section (either Disclosing or MdrReport). It must refer to the same element"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_901")
            )
          ),
          List(
            TableRow(Text("902"), None, "", None, None, Map("id" -> "code_902")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_902")),
            TableRow(
              HtmlContent("The MdrReport CorrDocRefId does not refer to the same previously sent MdrBody as the Disclosing element"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_902")
            )
          ),
          List(
            TableRow(Text("903"), None, "", None, None, Map("id" -> "code_903")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_903")),
            TableRow(
              HtmlContent("The Disclosing section contains resent data (DocTypeIndic = OECD0) so it must not have a CorrDocRefId"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_903")
            )
          ),
          List(
            TableRow(Text("904"), None, "", None, None, Map("id" -> "code_904")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_904")),
            TableRow(
              HtmlContent("MdrReport CrsAvoidance or OOS Reason is not one of the allowed values for the Disclosing Capacity provided"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_904")
            )
          ),
          List(
            TableRow(Text("905"), None, "", None, None, Map("id" -> "code_905")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_905")),
            TableRow(
              HtmlContent("Since the DocTypeIndic of Disclosing is OECD0, the DocTypeIndic of MdrReport must be OECD2"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_905")
            )
          ),
          List(
            TableRow(Text("906"), None, "", None, None, Map("id" -> "code_906")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_906")),
            TableRow(
              HtmlContent(
                "Since the MdrReport has a DocTypeIndic of OECD3, indicating this section must be deleted, this Disclosing section must be deleted too"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_906")
            )
          ),
          List(
            TableRow(Text("907"), None, "", None, None, Map("id" -> "code_907")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_907")),
            TableRow(
              HtmlContent(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, the Disclosing DocTypeIndic must contain the value of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_907")
            )
          ),
          List(
            TableRow(Text("908"), None, "", None, None, Map("id" -> "code_908")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_908")),
            TableRow(
              HtmlContent(
                "Since the MessageTypeIndic contains the value of MDR401 for new information, an MdrReport section must be provided with a DocTypeIndic of OECD1 for new information"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_908")
            )
          ),
          List(
            TableRow(Text("909"), None, "", None, None, Map("id" -> "code_909")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_909")),
            TableRow(
              HtmlContent("DocRefId must be 100 characters or less, start with your 15-character MDR ID and include up to 85 other characters of your choice"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_909")
            )
          ),
          List(
            TableRow(Text("910"), None, "", None, None, Map("id" -> "code_910")),
            TableRow(HtmlContent("N/A"), None, "", None, None, Map("id" -> "docRefId_910")),
            TableRow(
              HtmlContent(
                "MessageRefId must be 85 characters or less, start with your 15-character MDR ID and include up to 70 other characters of your choice"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_910")
            )
          ),
          List(
            TableRow(Text("911"), None, "", None, None, Map("id" -> "code_911")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_911")),
            TableRow(HtmlContent("Provide an issuedBy for every TIN that has a value other than NOTIN"), None, "", None, None, Map("id" -> "errorMessage_911"))
          ),
          List(
            TableRow(Text("912"), None, "", None, None, Map("id" -> "code_912")),
            TableRow(HtmlContent(""), None, "", None, None, Map("id" -> "docRefId_912")),
            TableRow(HtmlContent("The top level of the StructureChart must not have an Ownership or InvestAmount"),
                     None,
                     "",
                     None,
                     None,
                     Map("id" -> "errorMessage_912")
            )
          )
        ),
        header,
        Some("Errors"),
        "govuk-table__caption govuk-heading-m",
        false,
        "",
        Map()
      )

      val validationErrors = ValidationErrors(None, recordErrors)

      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable

    }

    "create table for all the allowed the custom file errors " in {

      val fileErrors = Some(
        Seq(
          FileErrors(FileErrorCode.CustomError, Some(FileRejectedViewModel.error_details_910)),
          FileErrors(FileErrorCode.FileContainsTestDataForProductionEnvironment, Some("error"))
        )
      )

      val expectedTable = Table(
        List(
          List(
            TableRow(Text("910"), None, "", None, None, Map("id" -> "code_910")),
            TableRow(HtmlContent("N/A"), None, "", None, None, Map("id" -> "docRefId_910")),
            TableRow(
              HtmlContent(
                "MessageRefId must be 85 characters or less, start with your 15-character MDR ID and include up to 70 other characters of your choice"
              ),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_910")
            )
          ),
          List(
            TableRow(Text("510"), None, "", None, None, Map("id" -> "code_510")),
            TableRow(Text("N/A"), None, "", None, None, Map("id" -> "docRefId_510")),
            TableRow(
              HtmlContent("We cannot accept test data so each DocTypeIndic must have a value of either OECD0, OECD1, OECD2 or OECD3"),
              None,
              "",
              None,
              None,
              Map("id" -> "errorMessage_510")
            )
          )
        ),
        header,
        Some("Errors"),
        "govuk-table__caption govuk-heading-m",
        false,
        "",
        Map()
      )

      val validationErrors = ValidationErrors(fileErrors, None)
      FileRejectedViewModel.createTable(validationErrors)(messages(app)) mustBe expectedTable
    }
  }

}
